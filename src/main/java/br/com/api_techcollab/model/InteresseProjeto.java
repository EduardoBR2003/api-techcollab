package br.com.api_techcollab.model;

import br.com.api_techcollab.model.enums.StatusInteresse;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "interesse_projeto")
public class InteresseProjeto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profissional_id", nullable = false)
    private Profissional profissional;

    @ManyToOne
    @JoinColumn(name = "vagaprojeto_id", nullable = false)
    private VagaProjeto vagaProjeto;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_interesse", nullable = false)
    private StatusInteresse statusInteresse;

    @Column(name = "mensagem_motivacao", columnDefinition = "TEXT")
    private String mensagemMotivacao;

    public InteresseProjeto() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Profissional getProfissional() { return profissional; }
    public void setProfissional(Profissional profissional) { this.profissional = profissional; }
    public VagaProjeto getVagaProjeto() { return vagaProjeto; }
    public void setVagaProjeto(VagaProjeto vagaProjeto) { this.vagaProjeto = vagaProjeto; }
    public StatusInteresse getStatusInteresse() { return statusInteresse; }
    public void setStatusInteresse(StatusInteresse statusInteresse) { this.statusInteresse = statusInteresse; }
    public String getMensagemMotivacao() { return mensagemMotivacao; }
    public void setMensagemMotivacao(String mensagemMotivacao) { this.mensagemMotivacao = mensagemMotivacao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InteresseProjeto that = (InteresseProjeto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}