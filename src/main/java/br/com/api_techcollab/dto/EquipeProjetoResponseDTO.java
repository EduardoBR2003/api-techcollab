package br.com.api_techcollab.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EquipeProjetoResponseDTO {
    private Long id;
    private Long projetoId;
    private String nomeEquipe;
    private Date dataFormacao;
    private List<MembroEquipeResponseDTO> membros;

    // MODIFICAÇÃO: Lista de links customizados
    private List<CustomLink> links = new ArrayList<>();

    public EquipeProjetoResponseDTO() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProjetoId() { return projetoId; }
    public void setProjetoId(Long projetoId) { this.projetoId = projetoId; }
    public String getNomeEquipe() { return nomeEquipe; }
    public void setNomeEquipe(String nomeEquipe) { this.nomeEquipe = nomeEquipe; }
    public Date getDataFormacao() { return dataFormacao; }
    public void setDataFormacao(Date dataFormacao) { this.dataFormacao = dataFormacao; }
    public List<MembroEquipeResponseDTO> getMembros() { return membros; }
    public void setMembros(List<MembroEquipeResponseDTO> membros) { this.membros = membros; }

    // MODIFICAÇÃO: Getter e Setter para os links
    public List<CustomLink> getLinks() { return links; }
    public void setLinks(List<CustomLink> links) { this.links = links; }
}