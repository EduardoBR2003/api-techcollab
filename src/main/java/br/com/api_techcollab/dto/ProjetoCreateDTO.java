package br.com.api_techcollab.dto;

import java.util.Date;

public class ProjetoCreateDTO {

    private String titulo;
    private String descDetalhada;
    private Double precoOfertado;
    private Date dataInicioPrevista;
    private Date dataConclusaoPrevista;

    public ProjetoCreateDTO() {}

    // Getters e Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescDetalhada() { return descDetalhada; }
    public void setDescDetalhada(String descDetalhada) { this.descDetalhada = descDetalhada; }
    public Double getPrecoOfertado() { return precoOfertado; }
    public void setPrecoOfertado(Double precoOfertado) { this.precoOfertado = precoOfertado; }
    public Date getDataInicioPrevista() { return dataInicioPrevista; }
    public void setDataInicioPrevista(Date dataInicioPrevista) { this.dataInicioPrevista = dataInicioPrevista; }
    public Date getDataConclusaoPrevista() { return dataConclusaoPrevista; }
    public void setDataConclusaoPrevista(Date dataConclusaoPrevista) { this.dataConclusaoPrevista = dataConclusaoPrevista; }
}