package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.EntregaProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntregaProjetoRepository extends JpaRepository<EntregaProjeto, Long> {

    /**
     * Encontra todas as entregas de um projeto específico.
     * @param projetoId O ID do projeto.
     * @return Uma lista de entregas.
     */
    List<EntregaProjeto> findByProjetoId(Long projetoId);
}