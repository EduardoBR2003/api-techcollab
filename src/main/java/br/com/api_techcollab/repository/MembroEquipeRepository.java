package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.MembroEquipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembroEquipeRepository extends JpaRepository<MembroEquipe, Long> {

    /**
     * Encontra todos os membros de uma equipe específica.
     * @param equipeProjetoId O ID da equipe.
     * @return Uma lista de membros da equipe.
     */
    List<MembroEquipe> findByEquipeProjetoId(Long equipeProjetoId);
}