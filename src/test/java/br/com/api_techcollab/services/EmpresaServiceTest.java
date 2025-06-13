package br.com.api_techcollab.services;

import br.com.api_techcollab.dto.EmpresaCreateDTO;
import br.com.api_techcollab.dto.EmpresaResponseDTO;
import br.com.api_techcollab.exceptions.BusinessException;
import br.com.api_techcollab.exceptions.ResourceNotFoundException;
import br.com.api_techcollab.model.Empresa;
import br.com.api_techcollab.repository.EmpresaRepository;
import br.com.api_techcollab.repository.ProjetoRepository;
import br.com.api_techcollab.repository.UsuarioRepository;
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
public class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProjetoRepository projetoRepository;

    @InjectMocks
    private EmpresaService empresaService;

    private Empresa empresa;
    private EmpresaCreateDTO empresaCreateDTO;

    @BeforeEach
    void setUp() {
        // Objeto Empresa que será usado como retorno dos mocks
        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNome("Empresa Teste");
        empresa.setEmail("teste@empresa.com");
        empresa.setCnpj("11.222.333/0001-44");
        empresa.setRazaoSocial("Empresa Teste LTDA");

        // DTO para criação de empresa
        empresaCreateDTO = new EmpresaCreateDTO();
        empresaCreateDTO.setNome("Empresa Nova");
        empresaCreateDTO.setEmail("nova@empresa.com");
        empresaCreateDTO.setCnpj("55.666.777/0001-88");
        empresaCreateDTO.setSenha("senha123");
    }

    @Test
    @DisplayName("Deve retornar uma empresa quando o ID for válido")
    void testFindById_Success() {
        // Arrange (Organizar)
        // Quando o método findById do repositório for chamado com 1L, retorne nossa empresa de teste.
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        // Act (Agir)
        // Chama o método do service que queremos testar.
        EmpresaResponseDTO result = empresaService.findById(1L);

        // Assert (Verificar)
        // Verifica se o resultado não é nulo e se os dados correspondem.
        assertNotNull(result);
        assertEquals("Empresa Teste", result.getNome());
        assertEquals(1L, result.getId());
        assertFalse(result.getLinks().isEmpty(), "O DTO de resposta deve conter links HATEOAS.");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID for inválido")
    void testFindById_NotFound() {
        // Arrange
        // Quando o findById for chamado com um ID que não existe (2L), retorne um Optional vazio.
        when(empresaRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        // Verifica se a exceção correta é lançada ao chamar o método.
        assertThrows(ResourceNotFoundException.class, () -> {
            empresaService.findById(2L);
        });
    }

    @Test
    @DisplayName("Deve criar uma nova empresa com sucesso")
    void testCreate_Success() {
        // Arrange
        // Garante que não existe usuário com o mesmo email ou cnpj
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(empresaRepository.findByCnpj(anyString())).thenReturn(Optional.empty());

        // Simula a empresa sendo salva e retornada com um ID
        Empresa empresaSalva = new Empresa();
        empresaSalva.setId(2L);
        empresaSalva.setNome(empresaCreateDTO.getNome());

        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresaSalva);
        // O findById é chamado no final do create para adicionar os links HATEOAS
        when(empresaRepository.findById(2L)).thenReturn(Optional.of(empresaSalva));


        // Act
        EmpresaResponseDTO result = empresaService.create(empresaCreateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Empresa Nova", result.getNome());
        assertEquals(2L, result.getId());
        verify(empresaRepository, times(1)).save(any(Empresa.class)); // Verifica se o save foi chamado 1 vez.
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar criar empresa com email já existente")
    void testCreate_EmailAlreadyExists() {
        // Arrange
        when(usuarioRepository.findByEmail("nova@empresa.com")).thenReturn(Optional.of(new Empresa()));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            empresaService.create(empresaCreateDTO);
        });

        assertEquals("O e-mail informado já está em uso.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar uma empresa que não possui projetos")
    void testDelete_Success() {
        // Arrange
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        // Simula que não há projetos para esta empresa
        when(projetoRepository.findByEmpresaId(1L)).thenReturn(Collections.emptyList());
        // Configura o mock para não fazer nada quando o delete for chamado
        doNothing().when(empresaRepository).delete(any(Empresa.class));

        // Act & Assert
        // Verifica que nenhuma exceção é lançada
        assertDoesNotThrow(() -> empresaService.delete(1L));
        // Verifica se o método delete foi efetivamente chamado no repositório
        verify(empresaRepository, times(1)).delete(empresa);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar deletar empresa com projetos associados")
    void testDelete_WithAssociatedProjects_ShouldThrowException() {
        // Arrange
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        // Simula que a empresa TEM projetos associados
        when(projetoRepository.findByEmpresaId(1L)).thenReturn(Collections.singletonList(new br.com.api_techcollab.model.Projeto()));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            empresaService.delete(1L);
        });

        assertTrue(exception.getMessage().contains("Não é possível excluir a empresa pois ela possui"));
        verify(empresaRepository, never()).delete(any(Empresa.class)); // Verifica que o delete NUNCA foi chamado.
    }
}