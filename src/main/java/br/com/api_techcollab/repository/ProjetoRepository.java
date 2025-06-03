package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    /**
     * Encontra todos os projetos pertencentes a uma empresa específica.
     * @param empresaId O ID da empresa.
     * @return Uma lista de projetos.
     */
    List<Projeto> findByEmpresaId(Long empresaId);
}