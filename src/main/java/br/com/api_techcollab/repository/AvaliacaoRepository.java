package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    /**
     * Encontra todas as avaliações de um projeto específico.
     * @param projetoId O ID do projeto.
     * @return Uma lista de avaliações.
     */
    List<Avaliacao> findByProjetoId(Long projetoId);
}