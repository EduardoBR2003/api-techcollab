package br.com.api_techcollab.dto;

import br.com.api_techcollab.model.enums.NivelExperiencia;
import java.util.List;

public class ProfissionalCreateDTO {

    private String nome;
    private String email;
    private String senha;
    private List<String> habilidades;
    private NivelExperiencia nivelExperiencia;
    private String curriculoUrl;

    public ProfissionalCreateDTO() {}

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public List<String> getHabilidades() { return habilidades; }
    public void setHabilidades(List<String> habilidades) { this.habilidades = habilidades; }
    public NivelExperiencia getNivelExperiencia() { return nivelExperiencia; }
    public void setNivelExperiencia(NivelExperiencia nivelExperiencia) { this.nivelExperiencia = nivelExperiencia; }
    public String getCurriculoUrl() { return curriculoUrl; }
    public void setCurriculoUrl(String curriculoUrl) { this.curriculoUrl = curriculoUrl; }
}