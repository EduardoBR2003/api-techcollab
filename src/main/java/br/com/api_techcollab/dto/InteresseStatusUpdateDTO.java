package br.com.api_techcollab.dto;

import br.com.api_techcollab.model.enums.StatusInteresse;

public class InteresseStatusUpdateDTO {

    private StatusInteresse statusInteresse;

    public InteresseStatusUpdateDTO() {}

    // Getters e Setters
    public StatusInteresse getStatusInteresse() { return statusInteresse; }
    public void setStatusInteresse(StatusInteresse statusInteresse) { this.statusInteresse = statusInteresse; }
}