package br.com.api_techcollab.dto;

import java.util.List;

public class EquipeMontarDTO {
    private List<Long> idsInteressesSelecionados;
    private String nomeEquipeSugerido;

    // Getters e Setters
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