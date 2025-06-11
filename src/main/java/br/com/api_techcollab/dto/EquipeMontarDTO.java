package br.com.api_techcollab.dto;

import java.util.List;

public class EquipeMontarDTO {

    private Long empresaId; // ID da empresa que está montando a equipe
    private List<Long> idsInteressesSelecionados;
    private String nomeEquipeSugerido;

    // Getter e Setter para empresaId
    public Long getEmpresaId() {
        return empresaId;
    }
    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    // Getters e Setters existentes
    public List<Long> getIdsInteressesSelecionados() {
        return idsInteressesSelecionados;
    }

    public void setIdsInteressesSelecionados(List<Long> idsInteressesSelecionados) {
        this.idsInteressesSelecionados = idsInteressesSelecionados;
    }

    public String getNomeEquipeSugerido() {
        return nomeEquipeSugerido;
    }

    public void setNomeEquipeSugerido(String nomeEquipeSugerido) {
        this.nomeEquipeSugerido = nomeEquipeSugerido;
    }
}