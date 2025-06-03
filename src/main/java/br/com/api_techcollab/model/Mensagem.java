package br.com.api_techcollab.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "mensagem")
public class Mensagem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "remetente_id", nullable = false)
    private Usuario remetente;

    @ManyToOne
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Usuario destinatario;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_envio", nullable = false)
    private Date dataEnvio;

    @Column(nullable = false)
    private Boolean lida = false;

    public Mensagem() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getRemetente() { return remetente; }
    public void setRemetente(Usuario remetente) { this.remetente = remetente; }
    public Usuario getDestinatario() { return destinatario; }
    public void setDestinatario(Usuario destinatario) { this.destinatario = destinatario; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public Date getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(Date dataEnvio) { this.dataEnvio = dataEnvio; }
    public Boolean getLida() { return lida; }
    public void setLida(Boolean lida) { this.lida = lida; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mensagem mensagem = (Mensagem) o;
        return Objects.equals(id, mensagem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}