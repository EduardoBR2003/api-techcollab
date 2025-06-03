package br.com.api_techcollab.dto;

import br.com.api_techcollab.model.enums.TipoAvaliado;

public class AvaliacaoCreateDTO {

    private Long projetoId;
    private Long avaliadoId;
    private TipoAvaliado tipoAvaliado;
    private Integer nota;
    private String comentario;

    public AvaliacaoCreateDTO() {}

    // Getters e Setters
    public Long getProjetoId() { return projetoId; }
    public void setProjetoId(Long projetoId) { this.projetoId = projetoId; }
    public Long getAvaliadoId() { return avaliadoId; }
    public void setAvaliadoId(Long avaliadoId) { this.avaliadoId = avaliadoId; }
    public TipoAvaliado getTipoAvaliado() { return tipoAvaliado; }
    public void setTipoAvaliado(TipoAvaliado tipoAvaliado) { this.tipoAvaliado = tipoAvaliado; }
    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}