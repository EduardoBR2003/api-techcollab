package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    // Futuros métodos de consulta específicos para Empresa podem ser adicionados aqui.
    Optional<Empresa> findByCnpj(String cnpj);
}