package br.com.api_techcollab.dto;

// DTO simplificado para informações básicas do profissional
public class ProfissionalSimpleResponseDTO {
    private Long id;
    private String nome;
    private String email;


    public ProfissionalSimpleResponseDTO() {}

    public ProfissionalSimpleResponseDTO(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}