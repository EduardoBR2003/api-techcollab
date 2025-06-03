package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo seu endereço de e-mail.
     * Essencial para a funcionalidade de login e verificação de e-mail existente.
     * @param email O e-mail a ser buscado.
     * @return um Optional contendo o usuário, se encontrado.
     */
    Optional<Usuario> findByEmail(String email);
}