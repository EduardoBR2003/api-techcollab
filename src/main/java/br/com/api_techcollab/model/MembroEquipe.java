package br.com.api_techcollab.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "membro_equipe")
public class MembroEquipe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "equipe_projeto_id", nullable = false)
    private EquipeProjeto equipeProjeto;

    @ManyToOne
    @JoinColumn(name = "profissional_id", nullable = false)
    private Profissional profissional;

    @Column(nullable = false, length = 50)
    private String papel;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_entrada", nullable = false)
    private Date dataEntrada;

    public MembroEquipe() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public EquipeProjeto getEquipeProjeto() { return equipeProjeto; }
    public void setEquipeProjeto(EquipeProjeto equipeProjeto) { this.equipeProjeto = equipeProjeto; }
    public Profissional getProfissional() { return profissional; }
    public void setProfissional(Profissional profissional) { this.profissional = profissional; }
    public String getPapel() { return papel; }
    public void setPapel(String papel) { this.papel = papel; }
    public Date getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(Date dataEntrada) { this.dataEntrada = dataEntrada; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MembroEquipe that = (MembroEquipe) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}