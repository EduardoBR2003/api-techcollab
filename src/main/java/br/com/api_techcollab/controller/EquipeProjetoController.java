package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.EquipeMontarDTO;
import br.com.api_techcollab.dto.EquipeProjetoResponseDTO;
import br.com.api_techcollab.services.EquipeProjetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projetos/{projetoId}/equipe")
public class EquipeProjetoController {

    @Autowired
    private EquipeProjetoService equipeProjetoService;

    // [RF008] Empresa monta a equipe - Refatorado
    @PostMapping
    public ResponseEntity<EquipeProjetoResponseDTO> montarEquipe(
            @PathVariable Long projetoId,
            @RequestBody EquipeMontarDTO equipeMontarDTO) {
        EquipeProjetoResponseDTO equipeMontada = equipeProjetoService.montarEquipe(
                projetoId,
                equipeMontarDTO.getIdsInteressesSelecionados(),
                equipeMontarDTO.getNomeEquipeSugerido(),
                equipeMontarDTO.getEmpresaId() // ID da empresa vem do DTO
        );
        return ResponseEntity.ok(equipeMontada);
    }

    @GetMapping
    public ResponseEntity<EquipeProjetoResponseDTO> visualizarEquipeProjeto(@PathVariable Long projetoId) {
        EquipeProjetoResponseDTO equipe = equipeProjetoService.visualizarEquipeProjeto(projetoId);
        return ResponseEntity.ok(equipe);
    }
}