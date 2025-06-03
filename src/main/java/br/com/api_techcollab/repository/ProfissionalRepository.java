package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {
    // Futuros métodos de consulta específicos para Profissional podem ser adicionados aqui.
}