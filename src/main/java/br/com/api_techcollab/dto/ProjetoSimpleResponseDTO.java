package br.com.api_techcollab.dto;

public class ProjetoSimpleResponseDTO {
    private Long id;
    private String titulo;

    public ProjetoSimpleResponseDTO() {}

    public ProjetoSimpleResponseDTO(Long id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
}