package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.EquipeProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipeProjetoRepository extends JpaRepository<EquipeProjeto, Long> {

    /**
     * Encontra a equipe associada a um projeto.
     * @param projetoId O ID do projeto.
     * @return Um Optional contendo a equipe, se existir.
     */
    Optional<EquipeProjeto> findByProjetoId(Long projetoId);
}