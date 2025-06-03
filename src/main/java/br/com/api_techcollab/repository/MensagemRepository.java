package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    // Métodos para buscar mensagens por remetente ou destinatário podem ser adicionados aqui.
}