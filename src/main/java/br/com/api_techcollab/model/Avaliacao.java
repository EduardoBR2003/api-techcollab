package br.com.api_techcollab.model;

import br.com.api_techcollab.model.enums.TipoAvaliado;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "avaliacao")
public class Avaliacao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    @ManyToOne
    @JoinColumn(name = "avaliador_id", nullable = false)
    private Usuario avaliador;

    @ManyToOne
    @JoinColumn(name = "avaliado_id", nullable = false)
    private Usuario avaliado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_avaliado", nullable = false)
    private TipoAvaliado tipoAvaliado;

    @Column(nullable = false)
    private Integer nota;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_avaliacao", nullable = false)
    private Date dataAvaliacao;

    public Avaliacao() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    public Usuario getAvaliador() { return avaliador; }
    public void setAvaliador(Usuario avaliador) { this.avaliador = avaliador; }
    public Usuario getAvaliado() { return avaliado; }
    public void setAvaliado(Usuario avaliado) { this.avaliado = avaliado; }
    public TipoAvaliado getTipoAvaliado() { return tipoAvaliado; }
    public void setTipoAvaliado(TipoAvaliado tipoAvaliado) { this.tipoAvaliado = tipoAvaliado; }
    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public Date getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(Date dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Avaliacao avaliacao = (Avaliacao) o;
        return Objects.equals(id, avaliacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}