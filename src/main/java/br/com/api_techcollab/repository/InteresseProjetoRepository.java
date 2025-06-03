package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.InteresseProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteresseProjetoRepository extends JpaRepository<InteresseProjeto, Long> {

    /**
     * Encontra todos os interesses manifestados por um profissional específico.
     * @param profissionalId O ID do profissional.
     * @return Uma lista de interesses.
     */
    List<InteresseProjeto> findByProfissionalId(Long profissionalId);

    /**
     * Encontra todos os interesses para uma vaga específica.
     * @param vagaProjetoId O ID da vaga.
     * @return Uma lista de interesses.
     */
    List<InteresseProjeto> findByVagaProjetoId(Long vagaProjetoId);
}