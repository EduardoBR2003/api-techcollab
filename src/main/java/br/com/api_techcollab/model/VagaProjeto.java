package br.com.api_techcollab.model;

import br.com.api_techcollab.model.enums.NivelExperiencia;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "vaga_projeto")
public class VagaProjeto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    @Column(name = "titulo_vaga", nullable = false, length = 100)
    private String tituloVaga;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "vaga_habilidades", joinColumns = @JoinColumn(name = "vaga_id"))
    @Column(name = "habilidade_requerida")
    private List<String> habilidadesRequeridas;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_exp_desejado", nullable = false)
    private NivelExperiencia nivelExpDesejado;

    @Column(name = "quant_funcionarios", nullable = false)
    private Integer quantFuncionarios;

    public VagaProjeto() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    public String getTituloVaga() { return tituloVaga; }
    public void setTituloVaga(String tituloVaga) { this.tituloVaga = tituloVaga; }
    public List<String> getHabilidadesRequeridas() { return habilidadesRequeridas; }
    public void setHabilidadesRequeridas(List<String> habilidadesRequeridas) { this.habilidadesRequeridas = habilidadesRequeridas; }
    public NivelExperiencia getNivelExpDesejado() { return nivelExpDesejado; }
    public void setNivelExpDesejado(NivelExperiencia nivelExpDesejado) { this.nivelExpDesejado = nivelExpDesejado; }
    public Integer getQuantFuncionarios() { return quantFuncionarios; }
    public void setQuantFuncionarios(Integer quantFuncionarios) { this.quantFuncionarios = quantFuncionarios; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VagaProjeto that = (VagaProjeto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}