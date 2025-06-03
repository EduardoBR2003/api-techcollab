package br.com.api_techcollab.dto;

public class EmpresaCreateDTO {

    private String nome;
    private String email;
    private String senha;
    private String cnpj;
    private String razaoSocial;
    private String descEmpresa;
    private String siteUrl;

    public EmpresaCreateDTO() {}

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
    public String getDescEmpresa() { return descEmpresa; }
    public void setDescEmpresa(String descEmpresa) { this.descEmpresa = descEmpresa; }
    public String getSiteUrl() { return siteUrl; }
    public void setSiteUrl(String siteUrl) { this.siteUrl = siteUrl; }
}