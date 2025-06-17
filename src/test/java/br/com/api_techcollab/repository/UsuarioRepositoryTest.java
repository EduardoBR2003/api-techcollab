package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.Profissional;
import br.com.api_techcollab.model.Usuario;
import br.com.api_techcollab.model.enums.TiposUsuarios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioRepositoryTest {

    @Mock // Cria um mock do UsuarioRepository.
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        // Inicializa uma entidade Usuario de teste (usando Profissional como subclasse concreta).
        usuario = new Profissional();
        usuario.setId(1L);
        usuario.setNome("Usuario Teste");
        usuario.setEmail("usuario.teste@example.com");
        usuario.setSenha("senhaSegura123");
        usuario.setTipoUsuario(TiposUsuarios.PROFISSIONAL);
        usuario.setDataCadastro(new Date());
    }

    @Test // Marca o método como um teste.
    @DisplayName("Deve encontrar um usuário pelo email") // Nome amigável para o teste.
    void findByEmail_ShouldReturnUsuario() {
        // Define o email a ser buscado.
        String email = "usuario.teste@example.com";
        // Configura o mock do repositório para retornar o usuário de teste quando findByEmail for chamado com o email.
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // Executa o método do repositório.
        Optional<Usuario> foundUsuario = usuarioRepository.findByEmail(email);

        // Verifica se o usuário foi encontrado e se o email corresponde.
        assertTrue(foundUsuario.isPresent());
        assertEquals(usuario.getEmail(), foundUsuario.get().getEmail());
    }

    @Test
    @DisplayName("Não deve encontrar usuário para email inexistente")
    void findByEmail_ShouldReturnEmptyOptional_WhenNotFound() {
        // Define um email que não existe.
        String nonExistentEmail = "naoexiste@example.com";
        // Configura o mock do repositório para retornar um Optional vazio quando findByEmail for chamado com o email inexistente.
        when(usuarioRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // Executa o método do repositório.
        Optional<Usuario> foundUsuario = usuarioRepository.findByEmail(nonExistentEmail);

        // Verifica se o usuário não foi encontrado.
        assertFalse(foundUsuario.isPresent());
    }
}