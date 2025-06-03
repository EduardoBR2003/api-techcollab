package br.com.api_techcollab.model;

import br.com.api_techcollab.model.enums.NivelExperiencia;
import br.com.api_techcollab.model.enums.TiposUsuarios;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "profissional")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Profissional extends Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "profissional_habilidades", joinColumns = @JoinColumn(name = "profissional_id"))
    @Column(name = "habilidade")
    private List<String> habilidades;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_experiencia")
    private NivelExperiencia nivelExperiencia;

    @Column(name = "curriculo_url", length = 255)
    private String curriculoUrl;

    public Profissional() {
        super();
        this.setTipoUsuario(TiposUsuarios.PROFISSIONAL);
    }

    // Getters e Setters
    public List<String> getHabilidades() { return habilidades; }
    public void setHabilidades(List<String> habilidades) { this.habilidades = habilidades; }
    public NivelExperiencia getNivelExperiencia() { return nivelExperiencia; }
    public void setNivelExperiencia(NivelExperiencia nivelExperiencia) { this.nivelExperiencia = nivelExperiencia; }
    public String getCurriculoUrl() { return curriculoUrl; }
    public void setCurriculoUrl(String curriculoUrl) { this.curriculoUrl = curriculoUrl; }
}