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

    @Mock // Cria um mock do EmpresaRepository.
    private EmpresaRepository empresaRepository;

    @Mock // Cria um mock do UsuarioRepository.
    private UsuarioRepository usuarioRepository;

    @Mock // Cria um mock do ProjetoRepository.
    private ProjetoRepository projetoRepository;

    @InjectMocks // Injeta os mocks no EmpresaService.
    private EmpresaService empresaService;

    private Empresa empresa;
    private EmpresaCreateDTO empresaCreateDTO;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        // Inicializa uma entidade Empresa de teste.
        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNome("Empresa Teste");
        empresa.setEmail("teste@empresa.com");
        empresa.setCnpj("11.222.333/0001-44");
        empresa.setRazaoSocial("Empresa Teste LTDA");

        // Inicializa um DTO para criação de empresa.
        empresaCreateDTO = new EmpresaCreateDTO();
        empresaCreateDTO.setNome("Empresa Nova");
        empresaCreateDTO.setEmail("nova@empresa.com");
        empresaCreateDTO.setCnpj("55.666.777/0001-88");
        empresaCreateDTO.setSenha("senha123");
    }

    @Test // Marca o método como um teste.
    @DisplayName("Deve retornar uma empresa quando o ID for válido") // Nome amigável para o teste.
    void testFindById_Success() {
        // Configura o mock do repositório para retornar a empresa de teste quando findById for chamado com o ID 1L.
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        // Executa o método do serviço que queremos testar.
        EmpresaResponseDTO result = empresaService.findById(1L);

        // Verifica se o resultado não é nulo, se os dados correspondem e se contém links HATEOAS.
        assertNotNull(result);
        assertEquals("Empresa Teste", result.getNome());
        assertEquals(1L, result.getId());
        assertFalse(result.getLinks().isEmpty(), "O DTO de resposta deve conter links HATEOAS.");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID for inválido")
    void testFindById_NotFound() {
        // Configura o mock para retornar um Optional vazio quando findById for chamado com um ID que não existe.
        when(empresaRepository.findById(2L)).thenReturn(Optional.empty());

        // Verifica se a exceção ResourceNotFoundException é lançada ao chamar o método.
        assertThrows(ResourceNotFoundException.class, () -> {
            empresaService.findById(2L);
        });
    }

    @Test
    @DisplayName("Deve criar uma nova empresa com sucesso")
    void testCreate_Success() {
        // Garante que não existe usuário com o mesmo email ou CNPJ no sistema.
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(empresaRepository.findByCnpj(anyString())).thenReturn(Optional.empty());

        // Simula a empresa sendo salva e retornada com um ID.
        Empresa empresaSalva = new Empresa();
        empresaSalva.setId(2L);
        empresaSalva.setNome(empresaCreateDTO.getNome());

        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresaSalva);
        // O findById é chamado no final do método create para adicionar os links HATEOAS.
        when(empresaRepository.findById(2L)).thenReturn(Optional.of(empresaSalva));

        // Executa o método do serviço.
        EmpresaResponseDTO result = empresaService.create(empresaCreateDTO);

        // Verifica se o resultado não é nulo, se o nome e o ID correspondem e se o método save foi chamado.
        assertNotNull(result);
        assertEquals("Empresa Nova", result.getNome());
        assertEquals(2L, result.getId());
        verify(empresaRepository, times(1)).save(any(Empresa.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar criar empresa com email já existente")
    void testCreate_EmailAlreadyExists() {
        // Configura o mock para simular que um usuário com o email já existe.
        when(usuarioRepository.findByEmail("nova@empresa.com")).thenReturn(Optional.of(new Empresa()));

        // Verifica se a exceção BusinessException é lançada ao tentar criar a empresa.
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            empresaService.create(empresaCreateDTO);
        });

        assertEquals("O e-mail informado já está em uso.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar uma empresa que não possui projetos")
    void testDelete_Success() {
        // Configura o mock para encontrar a empresa pelo ID e simular que não há projetos associados.
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(projetoRepository.findByEmpresaId(1L)).thenReturn(Collections.emptyList());
        // Configura o mock para não fazer nada quando o delete for chamado.
        doNothing().when(empresaRepository).delete(any(Empresa.class));

        // Verifica que nenhuma exceção é lançada ao deletar a empresa.
        assertDoesNotThrow(() -> empresaService.delete(1L));
        // Verifica se o método delete foi efetivamente chamado no repositório.
        verify(empresaRepository, times(1)).delete(empresa);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar deletar empresa com projetos associados")
    void testDelete_WithAssociatedProjects_ShouldThrowException() {
        // Configura o mock para simular que a empresa possui projetos associados.
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(projetoRepository.findByEmpresaId(1L)).thenReturn(Collections.singletonList(new br.com.api_techcollab.model.Projeto()));

        // Verifica se a exceção BusinessException é lançada ao tentar deletar a empresa.
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            empresaService.delete(1L);
        });

        assertTrue(exception.getMessage().contains("Não é possível excluir a empresa pois ela possui"));
        // Verifica que o método delete NUNCA foi chamado no repositório.
        verify(empresaRepository, never()).delete(any(Empresa.class));
    }
}