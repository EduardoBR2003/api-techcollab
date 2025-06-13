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

    @Mock
    private ProfissionalRepository profissionalRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private MembroEquipeRepository membroEquipeRepository;
    @Mock
    private InteresseProjetoRepository interesseProjetoRepository;

    @InjectMocks
    private ProfissionalService profissionalService;

    private Profissional profissional;
    private ProfissionalCreateDTO profissionalCreateDTO;

    @BeforeEach
    void setUp() {
        profissional = new Profissional();
        profissional.setId(1L);
        profissional.setNome("João da Silva");
        profissional.setEmail("joao.silva@dev.com");

        profissionalCreateDTO = new ProfissionalCreateDTO();
        profissionalCreateDTO.setNome("Maria Santos");
        profissionalCreateDTO.setEmail("maria.santos@dev.com");
        profissionalCreateDTO.setSenha("senha123");
    }

    @Test
    @DisplayName("Deve retornar um profissional quando o ID for válido")
    void testFindById_Success() {
        // Arrange
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));

        // Act
        ProfissionalResponseDTO result = profissionalService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("João da Silva", result.getNome());
        verify(profissionalRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID for inválido")
    void testFindById_NotFound() {
        // Arrange
        when(profissionalRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> profissionalService.findById(99L));
    }

    @Test
    @DisplayName("Deve criar um novo profissional com sucesso")
    void testCreate_Success() {
        // Arrange
        Profissional profissionalSalvo = new Profissional();
        profissionalSalvo.setId(2L);
        profissionalSalvo.setNome(profissionalCreateDTO.getNome());

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(profissionalRepository.save(any(Profissional.class))).thenReturn(profissionalSalvo);
        when(profissionalRepository.findById(2L)).thenReturn(Optional.of(profissionalSalvo));

        // Act
        ProfissionalResponseDTO result = profissionalService.create(profissionalCreateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Maria Santos", result.getNome());
        verify(profissionalRepository).save(any(Profissional.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar criar profissional com email já existente")
    void testCreate_EmailAlreadyExists() {
        // Arrange
        when(usuarioRepository.findByEmail("maria.santos@dev.com")).thenReturn(Optional.of(new Profissional()));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            profissionalService.create(profissionalCreateDTO);
        });
        assertEquals("O e-mail informado já está em uso.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar um profissional sem dependências")
    void testDelete_Success() {
        // Arrange
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(membroEquipeRepository.findAll()).thenReturn(Collections.emptyList());
        when(interesseProjetoRepository.findByProfissionalId(1L)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertDoesNotThrow(() -> profissionalService.delete(1L));
        verify(profissionalRepository, times(1)).delete(profissional);
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar deletar profissional com interesses ativos")
    void testDelete_WithActiveInterests() {
        // Arrange
        InteresseProjeto interesseAtivo = new InteresseProjeto();
        interesseAtivo.setStatusInteresse(StatusInteresse.PENDENTE);

        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(membroEquipeRepository.findAll()).thenReturn(Collections.emptyList());
        when(interesseProjetoRepository.findByProfissionalId(1L)).thenReturn(Collections.singletonList(interesseAtivo));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> profissionalService.delete(1L));
        assertTrue(exception.getMessage().contains("interesse(s) ativo(s) em projetos"));
        verify(profissionalRepository, never()).delete(any(Profissional.class));
    }
}