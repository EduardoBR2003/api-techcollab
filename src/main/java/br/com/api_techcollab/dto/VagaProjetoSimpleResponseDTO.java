package br.com.api_techcollab.dto;

// DTO simplificado para informações básicas da vaga
public class VagaProjetoSimpleResponseDTO {
    private Long id;
    private String tituloVaga;
    private Long projetoId;

    public VagaProjetoSimpleResponseDTO() {}

    public VagaProjetoSimpleResponseDTO(Long id, String tituloVaga, Long projetoId) {
        this.id = id;
        this.tituloVaga = tituloVaga;
        this.projetoId = projetoId;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTituloVaga() { return tituloVaga; }
    public void setTituloVaga(String tituloVaga) { this.tituloVaga = tituloVaga; }
    public Long getProjetoId() { return projetoId; }
    public void setProjetoId(Long projetoId) { this.projetoId = projetoId; }
}