package br.com.api_techcollab.dto;

public class InteresseCreateDTO {

    private Long vagaProjetoId;
    private String mensagemMotivacao;

    public InteresseCreateDTO() {}

    // Getters e Setters
    public Long getVagaProjetoId() { return vagaProjetoId; }
    public void setVagaProjetoId(Long vagaProjetoId) { this.vagaProjetoId = vagaProjetoId; }
    public String getMensagemMotivacao() { return mensagemMotivacao; }
    public void setMensagemMotivacao(String mensagemMotivacao) { this.mensagemMotivacao = mensagemMotivacao; }
}