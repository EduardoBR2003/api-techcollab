package br.com.api_techcollab.model;

import br.com.api_techcollab.model.enums.StatusEntrega;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "entrega_projeto")
public class EntregaProjeto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    @Column(name = "desc_entrega", nullable = false, columnDefinition = "TEXT")
    private String descEntrega;

    @Column(name = "arquivo_url", length = 255)
    private String arquivoUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_submissao")
    private Date dataSubmissao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_entrega", nullable = false)
    private StatusEntrega statusEntrega;

    public EntregaProjeto() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    public String getDescEntrega() { return descEntrega; }
    public void setDescEntrega(String descEntrega) { this.descEntrega = descEntrega; }
    public String getArquivoUrl() { return arquivoUrl; }
    public void setArquivoUrl(String arquivoUrl) { this.arquivoUrl = arquivoUrl; }
    public Date getDataSubmissao() { return dataSubmissao; }
    public void setDataSubmissao(Date dataSubmissao) { this.dataSubmissao = dataSubmissao; }
    public StatusEntrega getStatusEntrega() { return statusEntrega; }
    public void setStatusEntrega(StatusEntrega statusEntrega) { this.statusEntrega = statusEntrega; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntregaProjeto that = (EntregaProjeto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}