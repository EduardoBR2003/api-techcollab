package br.com.api_techcollab.dto;

public class EmpresaSimpleResponseDTO {

    private Long id;
    private String nome;
    private String razaoSocial;

    public EmpresaSimpleResponseDTO() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
}