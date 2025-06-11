package br.com.api_techcollab.dto;

import br.com.api_techcollab.model.enums.TiposUsuarios;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmpresaResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private TiposUsuarios tipoUsuario;
    private Date dataCadastro;
    private String cnpj;
    private String razaoSocial;
    private String descEmpresa;
    private String siteUrl;

    // MODIFICAÇÃO: Lista de links customizados
    private List<CustomLink> links = new ArrayList<>();

    public EmpresaResponseDTO() {}

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
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
    public String getDescEmpresa() { return descEmpresa; }
    public void setDescEmpresa(String descEmpresa) { this.descEmpresa = descEmpresa; }
    public String getSiteUrl() { return siteUrl; }
    public void setSiteUrl(String siteUrl) { this.siteUrl = siteUrl; }

    // MODIFICAÇÃO: Getter e Setter para os links
    public List<CustomLink> getLinks() { return links; }
    public void setLinks(List<CustomLink> links) { this.links = links; }
}