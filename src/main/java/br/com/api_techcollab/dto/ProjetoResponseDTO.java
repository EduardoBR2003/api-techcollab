package br.com.api_techcollab.dto;

import br.com.api_techcollab.model.enums.StatusProjeto;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;

public class ProjetoResponseDTO extends RepresentationModel<ProjetoResponseDTO> {

    private Long id;
    private String titulo;
    private String descDetalhada;
    private Double precoOfertado;
    private StatusProjeto statusProjeto;
    private Date dataInicioPrevista;
    private Date dataConclusaoPrevista;
    private EmpresaSimpleResponseDTO empresa; // DTO aninhado

    public ProjetoResponseDTO() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescDetalhada() { return descDetalhada; }
    public void setDescDetalhada(String descDetalhada) { this.descDetalhada = descDetalhada; }
    public Double getPrecoOfertado() { return precoOfertado; }
    public void setPrecoOfertado(Double precoOfertado) { this.precoOfertado = precoOfertado; }
    public StatusProjeto getStatusProjeto() { return statusProjeto; }
    public void setStatusProjeto(StatusProjeto statusProjeto) { this.statusProjeto = statusProjeto; }
    public Date getDataInicioPrevista() { return dataInicioPrevista; }
    public void setDataInicioPrevista(Date dataInicioPrevista) { this.dataInicioPrevista = dataInicioPrevista; }
    public Date getDataConclusaoPrevista() { return dataConclusaoPrevista; }
    public void setDataConclusaoPrevista(Date dataConclusaoPrevista) { this.dataConclusaoPrevista = dataConclusaoPrevista; }
    public EmpresaSimpleResponseDTO getEmpresa() { return empresa; }
    public void setEmpresa(EmpresaSimpleResponseDTO empresa) { this.empresa = empresa; }
}