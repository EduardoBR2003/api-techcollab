// src/test/java/br/com/api_techcollab/repository/UsuarioRepositoryTest.java
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

    @Mock
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Profissional(); // Usando uma subclasse concreta de Usuario
        usuario.setId(1L);
        usuario.setNome("Usuario Teste");
        usuario.setEmail("usuario.teste@example.com");
        usuario.setSenha("senhaSegura123");
        usuario.setTipoUsuario(TiposUsuarios.PROFISSIONAL);
        usuario.setDataCadastro(new Date());
    }

    @Test
    @DisplayName("Deve encontrar um usuário pelo email")
    void findByEmail_ShouldReturnUsuario() {
        // Arrange
        String email = "usuario.teste@example.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> foundUsuario = usuarioRepository.findByEmail(email);

        // Assert
        assertTrue(foundUsuario.isPresent());
        assertEquals(usuario.getEmail(), foundUsuario.get().getEmail());
    }

    @Test
    @DisplayName("Não deve encontrar usuário para email inexistente")
    void findByEmail_ShouldReturnEmptyOptional_WhenNotFound() {
        // Arrange
        String nonExistentEmail = "naoexiste@example.com";
        when(usuarioRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> foundUsuario = usuarioRepository.findByEmail(nonExistentEmail);

        // Assert
        assertFalse(foundUsuario.isPresent());
    }
}