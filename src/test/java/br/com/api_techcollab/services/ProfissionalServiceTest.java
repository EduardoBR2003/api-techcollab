package br.com.api_techcollab.services;

import br.com.api_techcollab.dto.ProfissionalCreateDTO;
import br.com.api_techcollab.dto.ProfissionalResponseDTO;
import br.com.api_techcollab.exceptions.BusinessException;
import br.com.api_techcollab.exceptions.ResourceNotFoundException;
import br.com.api_techcollab.model.InteresseProjeto;
import br.com.api_techcollab.model.Profissional;
import br.com.api_techcollab.model.enums.StatusInteresse;
import br.com.api_techcollab.repository.InteresseProjetoRepository;
import br.com.api_techcollab.repository.MembroEquipeRepository;
import br.com.api_techcollab.repository.ProfissionalRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfissionalServiceTest {

    @Mock // Cria um mock do ProfissionalRepository.
    private ProfissionalRepository profissionalRepository;
    @Mock // Cria um mock do UsuarioRepository.
    private UsuarioRepository usuarioRepository;
    @Mock // Cria um mock do MembroEquipeRepository.
    private MembroEquipeRepository membroEquipeRepository;
    @Mock // Cria um mock do InteresseProjetoRepository.
    private InteresseProjetoRepository interesseProjetoRepository;

    @InjectMocks // Injeta os mocks no ProfissionalService.
    private ProfissionalService profissionalService;

    private Profissional profissional;
    private ProfissionalCreateDTO profissionalCreateDTO;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        // Inicializa uma entidade Profissional de teste.
        profissional = new Profissional();
        profissional.setId(1L);
        profissional.setNome("João da Silva");
        profissional.setEmail("joao.silva@dev.com");

        // Inicializa um DTO para criação de profissional.
        profissionalCreateDTO = new ProfissionalCreateDTO();
        profissionalCreateDTO.setNome("Maria Santos");
        profissionalCreateDTO.setEmail("maria.santos@dev.com");
        profissionalCreateDTO.setSenha("senha123");
    }

    @Test // Marca o método como um teste.
    @DisplayName("Deve retornar um profissional quando o ID for válido") // Nome amigável para o teste.
    void testFindById_Success() {
        // Configura o mock do repositório para retornar o profissional de teste quando findById for chamado.
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));

        // Executa o método do serviço.
        ProfissionalResponseDTO result = profissionalService.findById(1L);

        // Verifica se o resultado não é nulo, se o nome corresponde e se o método findById foi chamado.
        assertNotNull(result);
        assertEquals("João da Silva", result.getNome());
        verify(profissionalRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID for inválido")
    void testFindById_NotFound() {
        // Configura o mock para retornar um Optional vazio quando findById for chamado com um ID inválido.
        when(profissionalRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Verifica se ResourceNotFoundException é lançada.
        assertThrows(ResourceNotFoundException.class, () -> profissionalService.findById(99L));
    }

    @Test
    @DisplayName("Deve criar um novo profissional com sucesso")
    void testCreate_Success() {
        // Configura os mocks para simular a criação bem-sucedida de um profissional.
        Profissional profissionalSalvo = new Profissional();
        profissionalSalvo.setId(2L);
        profissionalSalvo.setNome(profissionalCreateDTO.getNome());

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty()); // Email não existe
        when(profissionalRepository.save(any(Profissional.class))).thenReturn(profissionalSalvo);
        when(profissionalRepository.findById(2L)).thenReturn(Optional.of(profissionalSalvo)); // Simula o retorno para HATEOAS

        // Executa o método do serviço.
        ProfissionalResponseDTO result = profissionalService.create(profissionalCreateDTO);

        // Verifica se o resultado não é nulo, se o nome corresponde e se o método save foi chamado.
        assertNotNull(result);
        assertEquals("Maria Santos", result.getNome());
        verify(profissionalRepository).save(any(Profissional.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar criar profissional com email já existente")
    void testCreate_EmailAlreadyExists() {
        // Configura o mock para simular que um usuário com o email já existe.
        when(usuarioRepository.findByEmail("maria.santos@dev.com")).thenReturn(Optional.of(new Profissional()));

        // Verifica se BusinessException é lançada e se a mensagem está correta.
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            profissionalService.create(profissionalCreateDTO);
        });
        assertEquals("O e-mail informado já está em uso.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar um profissional sem dependências")
    void testDelete_Success() {
        // Configura os mocks para simular que o profissional não tem dependências ativas.
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(membroEquipeRepository.findAll()).thenReturn(Collections.emptyList()); // Não é membro de equipe
        when(interesseProjetoRepository.findByProfissionalId(1L)).thenReturn(Collections.emptyList()); // Não tem interesses ativos

        // Verifica que nenhuma exceção é lançada ao deletar.
        assertDoesNotThrow(() -> profissionalService.delete(1L));
        // Verifica se o método delete foi chamado.
        verify(profissionalRepository, times(1)).delete(profissional);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar deletar profissional com interesses ativos")
    void testDelete_WithActiveInterests() {
        // Configura os mocks para simular que o profissional tem um interesse ativo.
        InteresseProjeto interesseAtivo = new InteresseProjeto();
        interesseAtivo.setStatusInteresse(StatusInteresse.PENDENTE);

        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(membroEquipeRepository.findAll()).thenReturn(Collections.emptyList());
        when(interesseProjetoRepository.findByProfissionalId(1L)).thenReturn(Collections.singletonList(interesseAtivo));

        // Verifica se BusinessException é lançada e se a mensagem contém a substring esperada.
        BusinessException exception = assertThrows(BusinessException.class, () -> profissionalService.delete(1L));
        assertTrue(exception.getMessage().contains("interesse(s) ativo(s) em projetos"));
        // Verifica que o método delete NUNCA foi chamado.
        verify(profissionalRepository, never()).delete(any(Profissional.class));
    }
}