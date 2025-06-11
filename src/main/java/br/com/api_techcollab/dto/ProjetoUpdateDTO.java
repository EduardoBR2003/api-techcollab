package br.com.api_techcollab.dto;

import br.com.api_techcollab.model.enums.StatusProjeto;
import java.util.Date;

public class ProjetoUpdateDTO {

    // ID da empresa que está realizando a ação, para validação no service
    private Long empresaId;

    private String titulo;
    private String descDetalhada;
    private Double precoOfertado;
    private Date dataInicioPrevista;
    private Date dataConclusaoPrevista;
    private StatusProjeto statusProjeto;

    public ProjetoUpdateDTO() {}

    // Getter e Setter para empresaId
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }

    // Getters e Setters existentes
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
    public StatusProjeto getStatusProjeto() { return statusProjeto; }
    public void setStatusProjeto(StatusProjeto statusProjeto) { this.statusProjeto = statusProjeto; }
}