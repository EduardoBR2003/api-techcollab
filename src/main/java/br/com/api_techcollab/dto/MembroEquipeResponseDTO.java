package br.com.api_techcollab.dto;

import java.util.Date;

public class MembroEquipeResponseDTO {
    private Long id;
    private ProfissionalSimpleResponseDTO profissional;
    private String papel;
    private Date dataEntrada;

    public MembroEquipeResponseDTO() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProfissionalSimpleResponseDTO getProfissional() { return profissional; }
    public void setProfissional(ProfissionalSimpleResponseDTO profissional) { this.profissional = profissional; }
    public String getPapel() { return papel; }
    public void setPapel(String papel) { this.papel = papel; }
    public Date getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(Date dataEntrada) { this.dataEntrada = dataEntrada; }
}