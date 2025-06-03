package br.com.api_techcollab.model;

import br.com.api_techcollab.model.enums.StatusProjeto;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "projeto")
public class Projeto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(name = "desc_detalhada", columnDefinition = "TEXT", nullable = false)
    private String descDetalhada;

    @Column(name = "preco_ofertado")
    private Double precoOfertado;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_projeto", nullable = false)
    private StatusProjeto statusProjeto;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_inicio_prevista")
    private Date dataInicioPrevista;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_conclusao_prevista")
    private Date dataConclusaoPrevista;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    public Projeto() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescDetalhada() { return descDetalhada; }
    public void setDescDetalhada(String descDetalhada) { this.descDetalhada = descDetalhada; }
    public Double getPrecoOfertado() { return precoOfertado; }
    public void setPrecoOfertado(Double precoOfertado) { this.precoOfertado = precoOfertado; }
    public StatusProjeto getStatusProjeto() { return statusProjeto; }
    public void setStatusProjeto(StatusProjeto statusProjeto) { this.statusProjeto = statusProjeto; }
    public Date getDataInicioPrevista() { return dataInicioPrevista; }
    public void setDataInicioPrevista(Date dataInicioPrevista) { this.dataInicioPrevista = dataInicioPrevista; }
    public Date getDataConclusaoPrevista() { return dataConclusaoPrevista; }
    public void setDataConclusaoPrevista(Date dataConclusaoPrevista) { this.dataConclusaoPrevista = dataConclusaoPrevista; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Projeto projeto = (Projeto) o;
        return Objects.equals(id, projeto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}