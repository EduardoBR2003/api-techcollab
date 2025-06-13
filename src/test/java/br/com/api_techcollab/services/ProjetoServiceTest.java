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

    @Mock
    private ProjetoRepository projetoRepository;
    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private VagaProjetoRepository vagaProjetoRepository;

    @InjectMocks
    private ProjetoService projetoService;

    private Empresa empresa;
    private Projeto projeto;
    private ProjetoCreateDTO projetoCreateDTO;

    @BeforeEach
    void setUp() {
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

    @Test
    @DisplayName("Deve criar um projeto com sucesso para uma empresa válida")
    void testCriarProjeto_Success() {
        // Arrange
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);
        // O findById é chamado no final para adicionar HATEOAS
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));

        // Act
        ProjetoResponseDTO result = projetoService.criarProjeto(projetoCreateDTO, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Projeto Teste", result.getTitulo());
        verify(projetoRepository).save(any(Projeto.class));
    }

    @Test
    @DisplayName("Deve editar um projeto com sucesso")
    void testEditarProjeto_Success() {
        // Arrange
        ProjetoUpdateDTO updateDTO = new ProjetoUpdateDTO();
        updateDTO.setTitulo("Projeto Teste Atualizado");

        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);

        // Act
        ProjetoResponseDTO result = projetoService.editarProjeto(10L, updateDTO, 1L); // Empresa 1L é a dona

        // Assert
        assertNotNull(result);
        assertEquals("Projeto Teste Atualizado", result.getTitulo());
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao tentar editar projeto de outra empresa")
    void testEditarProjeto_AccessDenied() {
        // Arrange
        ProjetoUpdateDTO updateDTO = new ProjetoUpdateDTO();
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));

        // Act & Assert
        // A empresa 2L tenta editar o projeto da empresa 1L
        assertThrows(AccessDeniedException.class, () -> {
            projetoService.editarProjeto(10L, updateDTO, 2L);
        });
    }

    @Test
    @DisplayName("Deve excluir um projeto com status ABERTO_PARA_INTERESSE")
    void testExcluirProjeto_Success() {
        // Arrange
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));

        // Act & Assert
        assertDoesNotThrow(() -> projetoService.excluirProjeto(10L, 1L));
        verify(vagaProjetoRepository, times(1)).deleteAll(any());
        verify(projetoRepository, times(1)).delete(projeto);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar excluir projeto em DESENVOLVIMENTO")
    void testExcluirProjeto_InvalidStatus() {
        // Arrange
        projeto.setStatusProjeto(StatusProjeto.DESENVOLVIMENTO);
        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            projetoService.excluirProjeto(10L, 1L);
        });
        assertEquals("Projeto não pode ser excluído pois está no status: DESENVOLVIMENTO", exception.getMessage());
    }
}