package br.com.api_techcollab.services;

import br.com.api_techcollab.dto.VagaProjetoCreateDTO;
import br.com.api_techcollab.dto.VagaProjetoResponseDTO;
import br.com.api_techcollab.exceptions.AccessDeniedException;
import br.com.api_techcollab.exceptions.BusinessException;
import br.com.api_techcollab.model.Empresa;
import br.com.api_techcollab.model.Projeto;
import br.com.api_techcollab.model.VagaProjeto;
import br.com.api_techcollab.model.enums.NivelExperiencia;
import br.com.api_techcollab.model.enums.StatusProjeto;
import br.com.api_techcollab.repository.InteresseProjetoRepository;
import br.com.api_techcollab.repository.ProjetoRepository;
import br.com.api_techcollab.repository.VagaProjetoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VagaProjetoServiceTest {

    @Mock // Cria um mock do VagaProjetoRepository.
    private VagaProjetoRepository vagaProjetoRepository;
    @Mock // Cria um mock do ProjetoRepository.
    private ProjetoRepository projetoRepository;
    @Mock // Cria um mock do InteresseProjetoRepository.
    private InteresseProjetoRepository interesseProjetoRepository;

    @InjectMocks // Injeta os mocks no VagaProjetoService.
    private VagaProjetoService vagaProjetoService;

    private Projeto projeto;
    private VagaProjeto vaga;
    private VagaProjetoCreateDTO vagaCreateDTO;
    private Empresa empresa;


    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        // Inicializa as entidades e DTOs de teste.
        empresa = new Empresa();
        empresa.setId(1L);

        projeto = new Projeto();
        projeto.setId(10L);
        projeto.setEmpresa(empresa);
        projeto.setStatusProjeto(StatusProjeto.ABERTO_PARA_INTERESSE);

        vaga = new VagaProjeto();
        vaga.setId(100L);
        vaga.setTituloVaga("Desenvolvedor Java");
        vaga.setProjeto(projeto);

        vagaCreateDTO = new VagaProjetoCreateDTO();
        vagaCreateDTO.setTituloVaga("Desenvolvedor Backend");
        vagaCreateDTO.setNivelExpDesejado(NivelExperiencia.PLENO);
    }

    @Test // Marca o método como um teste.
    @DisplayName("Deve criar uma vaga para um projeto com sucesso") // Nome amigável para o teste.
    void testCriarVagaParaProjeto_Success() {
        // Configura os mocks para simular a criação bem-sucedida de uma vaga.
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));
        when(vagaProjetoRepository.save(any(VagaProjeto.class))).thenReturn(vaga);

        // Executa o método do serviço.
        VagaProjetoResponseDTO result = vagaProjetoService.criarVagaParaProjeto(10L, vagaCreateDTO);

        // Verifica se o resultado não é nulo, se o título da vaga corresponde e se o método save foi chamado.
        assertNotNull(result);
        assertEquals("Desenvolvedor Java", result.getTituloVaga());
        verify(vagaProjetoRepository, times(1)).save(any(VagaProjeto.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar criar vaga em projeto concluído")
    void testCriarVagaParaProjeto_InvalidStatus() {
        // Configura o mock para o projeto estar em status "CONCLUIDO".
        projeto.setStatusProjeto(StatusProjeto.CONCLUIDO);
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));

        // Verifica se BusinessException é lançada.
        assertThrows(BusinessException.class, () -> {
            vagaProjetoService.criarVagaParaProjeto(10L, vagaCreateDTO);
        });
    }

    @Test
    @DisplayName("Deve excluir uma vaga com sucesso")
    void testExcluirVagaProjeto_Success() {
        // Configura os mocks para simular a exclusão bem-sucedida de uma vaga sem interesses ativos.
        when(vagaProjetoRepository.findById(100L)).thenReturn(Optional.of(vaga));
        when(interesseProjetoRepository.findByVagaProjetoId(100L)).thenReturn(Collections.emptyList()); // Simula que não há interesses na vaga

        // Verifica que nenhuma exceção é lançada.
        assertDoesNotThrow(() -> vagaProjetoService.excluirVagaProjeto(100L, 1L));
        // Verifica se o método delete foi chamado.
        verify(vagaProjetoRepository, times(1)).delete(vaga);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao tentar excluir vaga de outra empresa")
    void testExcluirVagaProjeto_AccessDenied() {
        // Configura o mock para a vaga pertencer a um projeto de outra empresa.
        when(vagaProjetoRepository.findById(100L)).thenReturn(Optional.of(vaga));

        // Verifica se AccessDeniedException é lançada quando uma empresa não autorizada tenta excluir a vaga.
        assertThrows(AccessDeniedException.class, () -> {
            vagaProjetoService.excluirVagaProjeto(100L, 2L); // Empresa 2L tenta excluir a vaga que pertence ao projeto da empresa 1L
        });
    }
}