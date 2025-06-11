package br.com.api_techcollab.dto;

import br.com.api_techcollab.model.enums.StatusInteresse;
import java.util.ArrayList;
import java.util.List;

public class InteresseProjetoResponseDTO {
    private Long id;
    private ProfissionalSimpleResponseDTO profissional;
    private VagaProjetoSimpleResponseDTO vagaProjeto;
    private ProjetoSimpleResponseDTO projeto;
    private StatusInteresse statusInteresse;
    private String mensagemMotivacao;

    // MODIFICAÇÃO: Lista de links customizados
    private List<CustomLink> links = new ArrayList<>();

    public InteresseProjetoResponseDTO() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProfissionalSimpleResponseDTO getProfissional() { return profissional; }
    public void setProfissional(ProfissionalSimpleResponseDTO profissional) { this.profissional = profissional; }
    public VagaProjetoSimpleResponseDTO getVagaProjeto() { return vagaProjeto; }
    public void setVagaProjeto(VagaProjetoSimpleResponseDTO vagaProjeto) { this.vagaProjeto = vagaProjeto; }
    public ProjetoSimpleResponseDTO getProjeto() { return projeto; }
    public void setProjeto(ProjetoSimpleResponseDTO projeto) { this.projeto = projeto; }
    public StatusInteresse getStatusInteresse() { return statusInteresse; }
    public void setStatusInteresse(StatusInteresse statusInteresse) { this.statusInteresse = statusInteresse; }
    public String getMensagemMotivacao() { return mensagemMotivacao; }
    public void setMensagemMotivacao(String mensagemMotivacao) { this.mensagemMotivacao = mensagemMotivacao; }

    // MODIFICAÇÃO: Getter e Setter para os links
    public List<CustomLink> getLinks() { return links; }
    public void setLinks(List<CustomLink> links) { this.links = links; }
}