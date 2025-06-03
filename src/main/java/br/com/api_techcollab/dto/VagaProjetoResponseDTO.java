package br.com.api_techcollab.dto;

import br.com.api_techcollab.model.enums.NivelExperiencia;
import java.util.List;

public class VagaProjetoResponseDTO {

    private Long id;
    private Long projetoId;
    private String tituloVaga;
    private List<String> habilidadesRequeridas;
    private NivelExperiencia nivelExpDesejado;
    private Integer quantFuncionarios;

    public VagaProjetoResponseDTO() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProjetoId() { return projetoId; }
    public void setProjetoId(Long projetoId) { this.projetoId = projetoId; }
    public String getTituloVaga() { return tituloVaga; }
    public void setTituloVaga(String tituloVaga) { this.tituloVaga = tituloVaga; }
    public List<String> getHabilidadesRequeridas() { return habilidadesRequeridas; }
    public void setHabilidadesRequeridas(List<String> habilidadesRequeridas) { this.habilidadesRequeridas = habilidadesRequeridas; }
    public NivelExperiencia getNivelExpDesejado() { return nivelExpDesejado; }
    public void setNivelExpDesejado(NivelExperiencia nivelExpDesejado) { this.nivelExpDesejado = nivelExpDesejado; }
    public Integer getQuantFuncionarios() { return quantFuncionarios; }
    public void setQuantFuncionarios(Integer quantFuncionarios) { this.quantFuncionarios = quantFuncionarios; }
}