package br.com.api_techcollab.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "equipe_projeto")
public class EquipeProjeto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "projeto_id", nullable = false, unique = true)
    private Projeto projeto;

    @Column(name = "nome_equipe", nullable = false, length = 100)
    private String nomeEquipe;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_formacao", nullable = false)
    private Date dataFormacao;

    public EquipeProjeto() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    public String getNomeEquipe() { return nomeEquipe; }
    public void setNomeEquipe(String nomeEquipe) { this.nomeEquipe = nomeEquipe; }
    public Date getDataFormacao() { return dataFormacao; }
    public void setDataFormacao(Date dataFormacao) { this.dataFormacao = dataFormacao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipeProjeto that = (EquipeProjeto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}