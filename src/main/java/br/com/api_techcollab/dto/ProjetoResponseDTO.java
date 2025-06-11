package br.com.api_techcollab.dto;

import br.com.api_techcollab.model.enums.StatusProjeto;
// import org.springframework.hateoas.RepresentationModel; // REMOVER ESTA LINHA
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// REMOVER "extends RepresentationModel<...>"
public class ProjetoResponseDTO {

    private Long id;
    private String titulo;
    private String descDetalhada;
    private Double precoOfertado;
    private StatusProjeto statusProjeto;
    private Date dataInicioPrevista;
    private Date dataConclusaoPrevista;
    private EmpresaSimpleResponseDTO empresa;

    // ADICIONAR a lista de links customizados
    private List<CustomLink> links = new ArrayList<>();

    public ProjetoResponseDTO() {}

    // Getters e Setters existentes...
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

    // ADICIONAR Getter e Setter para os links
    public List<CustomLink> getLinks() { return links; }
    public void setLinks(List<CustomLink> links) { this.links = links; }
}