package br.com.api_techcollab.dto;

import br.com.api_techcollab.model.enums.NivelExperiencia;
import java.util.List;

public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private List<String> habilidades;
    private NivelExperiencia nivelExperiencia;
    private String curriculoUrl;

    public UsuarioResponseDTO() {
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public List<String> getHabilidades() {
        return habilidades;
    }
    public void setHabilidades(List<String> habilidades) {
        this.habilidades = habilidades;
    }
    public NivelExperiencia getNivelExperiencia() {
        return nivelExperiencia;
    }
    public void setNivelExperiencia(NivelExperiencia nivelExperiencia) {
        this.nivelExperiencia = nivelExperiencia;
    }
    public String getCurriculoUrl() {
        return curriculoUrl;
    }
    public void setCurriculoUrl(String curriculoUrl) {
        this.curriculoUrl = curriculoUrl;
    }
}