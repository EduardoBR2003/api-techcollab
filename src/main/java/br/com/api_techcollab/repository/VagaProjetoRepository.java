package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.VagaProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VagaProjetoRepository extends JpaRepository<VagaProjeto, Long> {

    /**
     * Encontra todas as vagas de um projeto específico.
     * @param projetoId O ID do projeto.
     * @return Uma lista de vagas do projeto.
     */
    List<VagaProjeto> findByProjetoId(Long projetoId);
}