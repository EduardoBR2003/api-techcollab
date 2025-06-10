package br.com.api_techcollab.model.enums;

public enum StatusInteresse {
    PENDENTE,
    SELECIONADO, // Selecionado pela empresa para avaliação do profissional
    ALOCADO,     // Profissional aceitou e foi alocado à equipe
    RECUSADO_DO_PROF, // Profissional recusou a alocação
    RECUSADO_PELA_EMPRESA // Empresa recusou o interesse do profissional
}