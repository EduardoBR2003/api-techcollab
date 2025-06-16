// src/test/java/br/com/api_techcollab/services/InteresseProjetoServiceTest.java
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

    @Mock
    private InteresseProjetoRepository interesseProjetoRepository;
    @Mock
    private ProfissionalRepository profissionalRepository;
    @Mock
    private VagaProjetoService vagaProjetoService;
    @Mock
    private EquipeProjetoService equipeProjetoService;

    @InjectMocks
    private InteresseProjetoService interesseProjetoService;

    private Profissional profissional;
    private Empresa empresa;
    private Projeto projeto;
    private VagaProjeto vagaProjeto;
    private InteresseProjeto interesse;
    private InteresseCreateDTO interesseCreateDTO;
    private InteresseStatusUpdateDTO interesseStatusUpdateDTO;

    @BeforeEach
    void setUp() {
        // Entidades base
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

        // DTOs
        interesseCreateDTO = new InteresseCreateDTO();
        interesseCreateDTO.setVagaProjetoId(vagaProjeto.getId());
        interesseCreateDTO.setMensagemMotivacao("Olá, tenho interesse.");

        interesseStatusUpdateDTO = new InteresseStatusUpdateDTO();
        interesseStatusUpdateDTO.setStatusInteresse(StatusInteresse.SELECIONADO);
    }

    @Test
    @DisplayName("Deve manifestar interesse com sucesso")
    void manifestarInteresse_Success() {
        when(profissionalRepository.findById(profissional.getId())).thenReturn(Optional.of(profissional));
        when(vagaProjetoService.buscarVagaPorIdEntidade(vagaProjeto.getId())).thenReturn(vagaProjeto);
        when(interesseProjetoRepository.findByProfissionalId(profissional.getId())).thenReturn(Collections.emptyList());
        when(interesseProjetoRepository.save(any(InteresseProjeto.class))).thenReturn(interesse);

        InteresseProjetoResponseDTO response = interesseProjetoService.manifestarInteresse(profissional.getId(), interesseCreateDTO);

        assertNotNull(response);
        assertEquals(interesse.getId(), response.getId());
        assertEquals(StatusInteresse.PENDENTE, response.getStatusInteresse());
        verify(interesseProjetoRepository, times(1)).save(any(InteresseProjeto.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao manifestar interesse com profissional não encontrado")
    void manifestarInteresse_ProfissionalNotFound_ThrowsException() {
        when(profissionalRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                interesseProjetoService.manifestarInteresse(999L, interesseCreateDTO));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao manifestar interesse em projeto não aberto")
    void manifestarInteresse_ProjetoNotOpen_ThrowsException() {
        projeto.setStatusProjeto(StatusProjeto.DESENVOLVIMENTO); // Altera status do projeto
        when(profissionalRepository.findById(profissional.getId())).thenReturn(Optional.of(profissional));
        when(vagaProjetoService.buscarVagaPorIdEntidade(vagaProjeto.getId())).thenReturn(vagaProjeto);

        assertThrows(BusinessException.class, () ->
                interesseProjetoService.manifestarInteresse(profissional.getId(), interesseCreateDTO));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao manifestar interesse em vaga já interessada")
    void manifestarInteresse_AlreadyInterested_ThrowsException() {
        when(profissionalRepository.findById(profissional.getId())).thenReturn(Optional.of(profissional));
        when(vagaProjetoService.buscarVagaPorIdEntidade(vagaProjeto.getId())).thenReturn(vagaProjeto);
        when(interesseProjetoRepository.findByProfissionalId(profissional.getId())).thenReturn(Collections.singletonList(interesse)); // Já tem interesse

        assertThrows(BusinessException.class, () ->
                interesseProjetoService.manifestarInteresse(profissional.getId(), interesseCreateDTO));
    }

    @Test
    @DisplayName("Deve visualizar interessados por vaga com sucesso")
    void visualizarInteressadosPorVaga_Success() {
        when(vagaProjetoService.buscarVagaPorIdEntidade(vagaProjeto.getId())).thenReturn(vagaProjeto);
        when(interesseProjetoRepository.findByVagaProjetoId(vagaProjeto.getId())).thenReturn(Collections.singletonList(interesse));

        List<InteresseProjetoResponseDTO> response = interesseProjetoService.visualizarInteressadosPorVaga(vagaProjeto.getId(), empresa.getId());

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals(interesse.getId(), response.get(0).getId());
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao empresa não autorizada tentar visualizar interessados")
    void visualizarInteressadosPorVaga_AccessDenied_ThrowsException() {
        Empresa outraEmpresa = new Empresa();
        outraEmpresa.setId(99L);
        projeto.setEmpresa(outraEmpresa); // Vaga pertence a outra empresa

        when(vagaProjetoService.buscarVagaPorIdEntidade(vagaProjeto.getId())).thenReturn(vagaProjeto);

        assertThrows(AccessDeniedException.class, () ->
                interesseProjetoService.visualizarInteressadosPorVaga(vagaProjeto.getId(), empresa.getId())); // Empresa errada tenta visualizar
    }

    @Test
    @DisplayName("Deve atualizar status de interesse pela empresa com sucesso")
    void atualizarStatusInteresseEmpresa_Success() {
        interesse.setStatusInteresse(StatusInteresse.PENDENTE); // Define status inicial
        interesseStatusUpdateDTO.setStatusInteresse(StatusInteresse.SELECIONADO); // Define novo status

        when(interesseProjetoRepository.findById(interesse.getId())).thenReturn(Optional.of(interesse));
        when(interesseProjetoRepository.save(any(InteresseProjeto.class))).thenReturn(interesse);

        InteresseProjetoResponseDTO response = interesseProjetoService.atualizarStatusInteresseEmpresa(
                interesse.getId(), interesseStatusUpdateDTO, empresa.getId());

        assertNotNull(response);
        assertEquals(StatusInteresse.SELECIONADO, response.getStatusInteresse());
        verify(interesseProjetoRepository, times(1)).save(any(InteresseProjeto.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao empresa tentar atualizar status para inválido")
    void atualizarStatusInteresseEmpresa_InvalidStatus_ThrowsException() {
        interesse.setStatusInteresse(StatusInteresse.PENDENTE); // Define status inicial
        interesseStatusUpdateDTO.setStatusInteresse(StatusInteresse.ALOCADO); // Status inválido para empresa

        when(interesseProjetoRepository.findById(interesse.getId())).thenReturn(Optional.of(interesse));

        assertThrows(BusinessException.class, () ->
                interesseProjetoService.atualizarStatusInteresseEmpresa(interesse.getId(), interesseStatusUpdateDTO, empresa.getId()));
    }

    @Test
    @DisplayName("Deve profissional responder alocação com sucesso (ALOCADO)")
    void profissionalResponderAlocacao_Accept_Success() {
        interesse.setStatusInteresse(StatusInteresse.SELECIONADO); // Profissional só pode responder se "SELECIONADO"
        interesseStatusUpdateDTO.setStatusInteresse(StatusInteresse.ALOCADO); // Profissional aceita

        when(interesseProjetoRepository.findById(interesse.getId())).thenReturn(Optional.of(interesse));
        when(interesseProjetoRepository.save(any(InteresseProjeto.class))).thenReturn(interesse);
        doNothing().when(equipeProjetoService).adicionarMembroEquipePorInteresse(any(InteresseProjeto.class));

        InteresseProjetoResponseDTO response = interesseProjetoService.profissionalResponderAlocacao(
                interesse.getId(), interesseStatusUpdateDTO, profissional.getId());

        assertNotNull(response);
        assertEquals(StatusInteresse.ALOCADO, response.getStatusInteresse());
        verify(equipeProjetoService, times(1)).adicionarMembroEquipePorInteresse(interesse);
    }

    @Test
    @DisplayName("Deve profissional responder alocação com sucesso (RECUSADO_DO_PROF)")
    void profissionalResponderAlocacao_Decline_Success() {
        interesse.setStatusInteresse(StatusInteresse.SELECIONADO); // Profissional só pode responder se "SELECIONADO"
        interesseStatusUpdateDTO.setStatusInteresse(StatusInteresse.RECUSADO_DO_PROF); // Profissional recusa

        when(interesseProjetoRepository.findById(interesse.getId())).thenReturn(Optional.of(interesse));
        when(interesseProjetoRepository.save(any(InteresseProjeto.class))).thenReturn(interesse);

        InteresseProjetoResponseDTO response = interesseProjetoService.profissionalResponderAlocacao(
                interesse.getId(), interesseStatusUpdateDTO, profissional.getId());

        assertNotNull(response);
        assertEquals(StatusInteresse.RECUSADO_DO_PROF, response.getStatusInteresse());
        verify(equipeProjetoService, never()).adicionarMembroEquipePorInteresse(any(InteresseProjeto.class));
    }

    @Test
    @DisplayName("Deve consultar status de interesses por profissional com sucesso")
    void consultarStatusInteressesProfissional_Success() {
        when(profissionalRepository.existsById(profissional.getId())).thenReturn(true);
        when(interesseProjetoRepository.findByProfissionalId(profissional.getId())).thenReturn(Collections.singletonList(interesse));

        List<InteresseProjetoResponseDTO> response = interesseProjetoService.consultarStatusInteressesProfissional(profissional.getId());

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals(interesse.getId(), response.get(0).getId());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao consultar interesses de profissional não encontrado")
    void consultarStatusInteressesProfissional_ProfissionalNotFound_ThrowsException() {
        when(profissionalRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                interesseProjetoService.consultarStatusInteressesProfissional(999L));
    }
}