package br.com.api_techcollab.services;

import br.com.api_techcollab.dto.InteresseCreateDTO;
import br.com.api_techcollab.dto.InteresseProjetoResponseDTO;
import br.com.api_techcollab.dto.InteresseStatusUpdateDTO;
import br.com.api_techcollab.exceptions.AccessDeniedException;
import br.com.api_techcollab.exceptions.BusinessException;
import br.com.api_techcollab.exceptions.ResourceNotFoundException;
import br.com.api_techcollab.model.InteresseProjeto;
import br.com.api_techcollab.model.Profissional;
import br.com.api_techcollab.model.Projeto;
import br.com.api_techcollab.model.VagaProjeto;
import br.com.api_techcollab.model.Empresa;
import br.com.api_techcollab.model.enums.StatusInteresse;
import br.com.api_techcollab.model.enums.StatusProjeto;
import br.com.api_techcollab.repository.InteresseProjetoRepository;
import br.com.api_techcollab.repository.ProfissionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InteresseProjetoServiceTest {

    @Mock // Cria um mock do InteresseProjetoRepository.
    private InteresseProjetoRepository interesseProjetoRepository;
    @Mock // Cria um mock do ProfissionalRepository.
    private ProfissionalRepository profissionalRepository;
    @Mock // Cria um mock do VagaProjetoService.
    private VagaProjetoService vagaProjetoService;
    @Mock // Cria um mock do EquipeProjetoService.
    private EquipeProjetoService equipeProjetoService;

    @InjectMocks // Injeta os mocks no InteresseProjetoService.
    private InteresseProjetoService interesseProjetoService;

    private Profissional profissional;
    private Empresa empresa;
    private Projeto projeto;
    private VagaProjeto vagaProjeto;
    private InteresseProjeto interesse;
    private InteresseCreateDTO interesseCreateDTO;
    private InteresseStatusUpdateDTO interesseStatusUpdateDTO;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        // Inicializa as entidades e DTOs de teste.
        empresa = new Empresa();
        empresa.setId(1L);

        projeto = new Projeto();
        projeto.setId(10L);
        projeto.setEmpresa(empresa);
        projeto.setStatusProjeto(StatusProjeto.ABERTO_PARA_INTERESSE);

        vagaProjeto = new VagaProjeto();
        vagaProjeto.setId(100L);
        vagaProjeto.setProjeto(projeto);

        profissional = new Profissional();
        profissional.setId(1000L);

        interesse = new InteresseProjeto();
        interesse.setId(1L);
        interesse.setProfissional(profissional);
        interesse.setVagaProjeto(vagaProjeto);
        interesse.setStatusInteresse(StatusInteresse.PENDENTE);
        interesse.setMensagemMotivacao("Quero muito esta vaga!");

        interesseCreateDTO = new InteresseCreateDTO();
        interesseCreateDTO.setVagaProjetoId(vagaProjeto.getId());
        interesseCreateDTO.setMensagemMotivacao("Olá, tenho interesse.");

        interesseStatusUpdateDTO = new InteresseStatusUpdateDTO();
        // Definir um status para o DTO de atualização, se relevante
    }

    @Test // Marca o método como um teste.
    @DisplayName("Deve manifestar interesse com sucesso") // Nome amigável para o teste.
    void manifestarInteresse_Success() {
        // Configura os mocks para simular o cenário de manifestação de interesse bem-sucedida.
        when(profissionalRepository.findById(profissional.getId())).thenReturn(Optional.of(profissional));
        when(vagaProjetoService.buscarVagaPorIdEntidade(vagaProjeto.getId())).thenReturn(vagaProjeto);
        when(interesseProjetoRepository.findByProfissionalId(profissional.getId())).thenReturn(Collections.emptyList());
        when(interesseProjetoRepository.save(any(InteresseProjeto.class))).thenReturn(interesse);

        // Executa o método do serviço.
        InteresseProjetoResponseDTO response = interesseProjetoService.manifestarInteresse(profissional.getId(), interesseCreateDTO);

        // Verifica se a resposta não é nula, se o ID e status correspondem e se o método save foi chamado.
        assertNotNull(response);
        assertEquals(interesse.getId(), response.getId());
        assertEquals(StatusInteresse.PENDENTE, response.getStatusInteresse());
        verify(interesseProjetoRepository, times(1)).save(any(InteresseProjeto.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao manifestar interesse com profissional não encontrado")
    void manifestarInteresse_ProfissionalNotFound_ThrowsException() {
        // Configura o mock para não encontrar o profissional.
        when(profissionalRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Verifica se ResourceNotFoundException é lançada.
        assertThrows(ResourceNotFoundException.class, () ->
                interesseProjetoService.manifestarInteresse(999L, interesseCreateDTO));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao manifestar interesse em projeto não aberto")
    void manifestarInteresse_ProjetoNotOpen_ThrowsException() {
        // Configura o mock para o projeto estar em status que não permite interesse.
        projeto.setStatusProjeto(StatusProjeto.DESENVOLVIMENTO); // Altera status do projeto
        when(profissionalRepository.findById(profissional.getId())).thenReturn(Optional.of(profissional));
        when(vagaProjetoService.buscarVagaPorIdEntidade(vagaProjeto.getId())).thenReturn(vagaProjeto);

        // Verifica se BusinessException é lançada.
        assertThrows(BusinessException.class, () ->
                interesseProjetoService.manifestarInteresse(profissional.getId(), interesseCreateDTO));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao manifestar interesse em vaga já interessada")
    void manifestarInteresse_AlreadyInterested_ThrowsException() {
        // Configura o mock para simular que o profissional já manifestou interesse nesta vaga.
        when(profissionalRepository.findById(profissional.getId())).thenReturn(Optional.of(profissional));
        when(vagaProjetoService.buscarVagaPorIdEntidade(vagaProjeto.getId())).thenReturn(vagaProjeto);
        when(interesseProjetoRepository.findByProfissionalId(profissional.getId())).thenReturn(Collections.singletonList(interesse)); // Já tem interesse

        // Verifica se BusinessException é lançada.
        assertThrows(BusinessException.class, () ->
                interesseProjetoService.manifestarInteresse(profissional.getId(), interesseCreateDTO));
    }

    @Test
    @DisplayName("Deve visualizar interessados por vaga com sucesso")
    void visualizarInteressadosPorVaga_Success() {
        // Configura os mocks para simular a visualização de interessados.
        when(vagaProjetoService.buscarVagaPorIdEntidade(vagaProjeto.getId())).thenReturn(vagaProjeto);
        when(interesseProjetoRepository.findByVagaProjetoId(vagaProjeto.getId())).thenReturn(Collections.singletonList(interesse));

        // Executa o método do serviço.
        List<InteresseProjetoResponseDTO> response = interesseProjetoService.visualizarInteressadosPorVaga(vagaProjeto.getId(), empresa.getId());

        // Verifica se a resposta não é nula, não está vazia e o interesse corresponde.
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals(interesse.getId(), response.get(0).getId());
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao empresa não autorizada tentar visualizar interessados")
    void visualizarInteressadosPorVaga_AccessDenied_ThrowsException() {
        // Configura o mock para o projeto pertencer a outra empresa.
        Empresa outraEmpresa = new Empresa();
        outraEmpresa.setId(99L);
        projeto.setEmpresa(outraEmpresa); // Vaga pertence a outra empresa

        when(vagaProjetoService.buscarVagaPorIdEntidade(vagaProjeto.getId())).thenReturn(vagaProjeto);

        // Verifica se AccessDeniedException é lançada quando uma empresa não autorizada tenta visualizar.
        assertThrows(AccessDeniedException.class, () ->
                interesseProjetoService.visualizarInteressadosPorVaga(vagaProjeto.getId(), empresa.getId())); // Empresa errada tenta visualizar
    }

    @Test
    @DisplayName("Deve atualizar status de interesse pela empresa com sucesso")
    void atualizarStatusInteresseEmpresa_Success() {
        // Configura o mock para atualizar o status do interesse pela empresa.
        interesse.setStatusInteresse(StatusInteresse.PENDENTE); // Define status inicial
        interesseStatusUpdateDTO.setStatusInteresse(StatusInteresse.SELECIONADO); // Define novo status

        when(interesseProjetoRepository.findById(interesse.getId())).thenReturn(Optional.of(interesse));
        when(interesseProjetoRepository.save(any(InteresseProjeto.class))).thenReturn(interesse);

        // Executa o método do serviço.
        InteresseProjetoResponseDTO response = interesseProjetoService.atualizarStatusInteresseEmpresa(
                interesse.getId(), interesseStatusUpdateDTO, empresa.getId());

        // Verifica se o status foi atualizado e se o método save foi chamado.
        assertNotNull(response);
        assertEquals(StatusInteresse.SELECIONADO, response.getStatusInteresse());
        verify(interesseProjetoRepository, times(1)).save(any(InteresseProjeto.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao empresa tentar atualizar status para inválido")
    void atualizarStatusInteresseEmpresa_InvalidStatus_ThrowsException() {
        // Configura o mock para a empresa tentar atualizar para um status inválido.
        interesse.setStatusInteresse(StatusInteresse.PENDENTE); // Define status inicial
        interesseStatusUpdateDTO.setStatusInteresse(StatusInteresse.ALOCADO); // Status inválido para empresa

        when(interesseProjetoRepository.findById(interesse.getId())).thenReturn(Optional.of(interesse));

        // Verifica se BusinessException é lançada.
        assertThrows(BusinessException.class, () ->
                interesseProjetoService.atualizarStatusInteresseEmpresa(interesse.getId(), interesseStatusUpdateDTO, empresa.getId()));
    }

    @Test
    @DisplayName("Deve profissional responder alocação com sucesso (ALOCADO)")
    void profissionalResponderAlocacao_Accept_Success() {
        // Configura o mock para o profissional aceitar a alocação.
        interesse.setStatusInteresse(StatusInteresse.SELECIONADO); // Profissional só pode responder se "SELECIONADO"
        interesseStatusUpdateDTO.setStatusInteresse(StatusInteresse.ALOCADO); // Profissional aceita

        when(interesseProjetoRepository.findById(interesse.getId())).thenReturn(Optional.of(interesse));
        when(interesseProjetoRepository.save(any(InteresseProjeto.class))).thenReturn(interesse);
        doNothing().when(equipeProjetoService).adicionarMembroEquipePorInteresse(any(InteresseProjeto.class)); // Simula o serviço de equipe

        // Executa o método do serviço.
        InteresseProjetoResponseDTO response = interesseProjetoService.profissionalResponderAlocacao(
                interesse.getId(), interesseStatusUpdateDTO, profissional.getId());

        // Verifica se o status foi atualizado e se o método de adicionar membro à equipe foi chamado.
        assertNotNull(response);
        assertEquals(StatusInteresse.ALOCADO, response.getStatusInteresse());
        verify(equipeProjetoService, times(1)).adicionarMembroEquipePorInteresse(interesse);
    }

    @Test
    @DisplayName("Deve profissional responder alocação com sucesso (RECUSADO_DO_PROF)")
    void profissionalResponderAlocacao_Decline_Success() {
        // Configura o mock para o profissional recusar a alocação.
        interesse.setStatusInteresse(StatusInteresse.SELECIONADO); // Profissional só pode responder se "SELECIONADO"
        interesseStatusUpdateDTO.setStatusInteresse(StatusInteresse.RECUSADO_DO_PROF); // Profissional recusa

        when(interesseProjetoRepository.findById(interesse.getId())).thenReturn(Optional.of(interesse));
        when(interesseProjetoRepository.save(any(InteresseProjeto.class))).thenReturn(interesse);

        // Executa o método do serviço.
        InteresseProjetoResponseDTO response = interesseProjetoService.profissionalResponderAlocacao(
                interesse.getId(), interesseStatusUpdateDTO, profissional.getId());

        // Verifica se o status foi atualizado e se o método de adicionar membro à equipe NÃO foi chamado.
        assertNotNull(response);
        assertEquals(StatusInteresse.RECUSADO_DO_PROF, response.getStatusInteresse());
        verify(equipeProjetoService, never()).adicionarMembroEquipePorInteresse(any(InteresseProjeto.class));
    }

    @Test
    @DisplayName("Deve consultar status de interesses por profissional com sucesso")
    void consultarStatusInteressesProfissional_Success() {
        // Configura os mocks para simular a consulta de interesses do profissional.
        when(profissionalRepository.existsById(profissional.getId())).thenReturn(true);
        when(interesseProjetoRepository.findByProfissionalId(profissional.getId())).thenReturn(Collections.singletonList(interesse));

        // Executa o método do serviço.
        List<InteresseProjetoResponseDTO> response = interesseProjetoService.consultarStatusInteressesProfissional(profissional.getId());

        // Verifica se a resposta não é nula, não está vazia e o interesse corresponde.
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals(interesse.getId(), response.get(0).getId());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao consultar interesses de profissional não encontrado")
    void consultarStatusInteressesProfissional_ProfissionalNotFound_ThrowsException() {
        // Configura o mock para não encontrar o profissional.
        when(profissionalRepository.existsById(anyLong())).thenReturn(false);

        // Verifica se ResourceNotFoundException é lançada.
        assertThrows(ResourceNotFoundException.class, () ->
                interesseProjetoService.consultarStatusInteressesProfissional(999L));
    }
}