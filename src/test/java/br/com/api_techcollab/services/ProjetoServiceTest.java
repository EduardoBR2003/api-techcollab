package br.com.api_techcollab.services;

import br.com.api_techcollab.dto.ProjetoCreateDTO;
import br.com.api_techcollab.dto.ProjetoResponseDTO;
import br.com.api_techcollab.dto.ProjetoUpdateDTO;
import br.com.api_techcollab.exceptions.AccessDeniedException;
import br.com.api_techcollab.exceptions.BusinessException;
import br.com.api_techcollab.model.Empresa;
import br.com.api_techcollab.model.Projeto;
import br.com.api_techcollab.model.enums.StatusProjeto;
import br.com.api_techcollab.repository.EmpresaRepository;
import br.com.api_techcollab.repository.ProjetoRepository;
import br.com.api_techcollab.repository.VagaProjetoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjetoServiceTest {

    @Mock // Cria um mock do ProjetoRepository.
    private ProjetoRepository projetoRepository;
    @Mock // Cria um mock do EmpresaRepository.
    private EmpresaRepository empresaRepository;
    @Mock // Cria um mock do VagaProjetoRepository.
    private VagaProjetoRepository vagaProjetoRepository;

    @InjectMocks // Injeta os mocks no ProjetoService.
    private ProjetoService projetoService;

    private Empresa empresa;
    private Projeto projeto;
    private ProjetoCreateDTO projetoCreateDTO;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        // Inicializa as entidades e DTOs de teste.
        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNome("Empresa Mock");

        projeto = new Projeto();
        projeto.setId(10L);
        projeto.setTitulo("Projeto Teste");
        projeto.setEmpresa(empresa);
        projeto.setStatusProjeto(StatusProjeto.ABERTO_PARA_INTERESSE);

        projetoCreateDTO = new ProjetoCreateDTO();
        projetoCreateDTO.setTitulo("Novo Projeto");
        projetoCreateDTO.setDescDetalhada("Descrição do novo projeto.");
    }

    @Test // Marca o método como um teste.
    @DisplayName("Deve criar um projeto com sucesso para uma empresa válida") // Nome amigável para o teste.
    void testCriarProjeto_Success() {
        // Configura os mocks para simular a criação bem-sucedida de um projeto.
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);
        // O findById é chamado no final para adicionar HATEOAS.
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));

        // Executa o método do serviço.
        ProjetoResponseDTO result = projetoService.criarProjeto(projetoCreateDTO, 1L);

        // Verifica se o resultado não é nulo, se o título corresponde e se o método save foi chamado.
        assertNotNull(result);
        assertEquals("Projeto Teste", result.getTitulo());
        verify(projetoRepository).save(any(Projeto.class));
    }

    @Test
    @DisplayName("Deve editar um projeto com sucesso")
    void testEditarProjeto_Success() {
        // Inicializa o DTO de atualização.
        ProjetoUpdateDTO updateDTO = new ProjetoUpdateDTO();
        updateDTO.setTitulo("Projeto Teste Atualizado");

        // Configura os mocks para simular a edição bem-sucedida de um projeto.
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);

        // Executa o método do serviço.
        ProjetoResponseDTO result = projetoService.editarProjeto(10L, updateDTO, 1L); // Empresa 1L é a dona

        // Verifica se o resultado não é nulo e se o título foi atualizado.
        assertNotNull(result);
        assertEquals("Projeto Teste Atualizado", result.getTitulo());
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao tentar editar projeto de outra empresa")
    void testEditarProjeto_AccessDenied() {
        // Inicializa o DTO de atualização.
        ProjetoUpdateDTO updateDTO = new ProjetoUpdateDTO();
        // Configura o mock para encontrar o projeto.
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));

        // Verifica se AccessDeniedException é lançada quando uma empresa diferente tenta editar o projeto.
        assertThrows(AccessDeniedException.class, () -> {
            projetoService.editarProjeto(10L, updateDTO, 2L); // A empresa 2L tenta editar o projeto da empresa 1L
        });
    }

    @Test
    @DisplayName("Deve excluir um projeto com status ABERTO_PARA_INTERESSE")
    void testExcluirProjeto_Success() {
        // Configura o mock para encontrar o projeto e simular a exclusão de vagas associadas.
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));

        // Verifica que nenhuma exceção é lançada ao excluir o projeto.
        assertDoesNotThrow(() -> projetoService.excluirProjeto(10L, 1L));
        // Verifica se os métodos de exclusão foram chamados.
        verify(vagaProjetoRepository, times(1)).deleteAll(any());
        verify(projetoRepository, times(1)).delete(projeto);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar excluir projeto em DESENVOLVIMENTO")
    void testExcluirProjeto_InvalidStatus() {
        // Configura o mock para o projeto estar em status de desenvolvimento.
        projeto.setStatusProjeto(StatusProjeto.DESENVOLVIMENTO);
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));

        // Verifica se BusinessException é lançada e se a mensagem está correta.
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            projetoService.excluirProjeto(10L, 1L);
        });
        assertEquals("Projeto não pode ser excluído pois está no status: DESENVOLVIMENTO", exception.getMessage());
    }
}