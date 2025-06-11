package br.com.api_techcollab.dto;

import br.com.api_techcollab.model.enums.NivelExperiencia;
import br.com.api_techcollab.model.enums.TiposUsuarios;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfissionalResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private TiposUsuarios tipoUsuario;
    private Date dataCadastro;
    private List<String> habilidades;
    private NivelExperiencia nivelExperiencia;
    private String curriculoUrl;

    // MODIFICAÇÃO: Lista de links customizados
    private List<CustomLink> links = new ArrayList<>();

    public ProfissionalResponseDTO() {}

    // Getters e Setters existentes...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public TiposUsuarios getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TiposUsuarios tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    public Date getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(Date dataCadastro) { this.dataCadastro = dataCadastro; }
    public List<String> getHabilidades() { return habilidades; }
    public void setHabilidades(List<String> habilidades) { this.habilidades = habilidades; }
    public NivelExperiencia getNivelExperiencia() { return nivelExperiencia; }
    public void setNivelExperiencia(NivelExperiencia nivelExperiencia) { this.nivelExperiencia = nivelExperiencia; }
    public String getCurriculoUrl() { return curriculoUrl; }
    public void setCurriculoUrl(String curriculoUrl) { this.curriculoUrl = curriculoUrl; }

    // MODIFICAÇÃO: Getter e Setter para os links
    public List<CustomLink> getLinks() { return links; }
    public void setLinks(List<CustomLink> links) { this.links = links; }
}