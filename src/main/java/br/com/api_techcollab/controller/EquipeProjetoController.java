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

    // [RF008] Empresa monta a equipe - Agora com empresaId para validação
    @PostMapping("/empresa/{empresaId}")
    public ResponseEntity<EquipeProjetoResponseDTO> montarEquipe(
            @PathVariable Long projetoId,
            @PathVariable Long empresaId,
            @RequestBody EquipeMontarDTO equipeMontarDTO) {
        EquipeProjetoResponseDTO equipeMontada = equipeProjetoService.montarEquipe(
                projetoId,
                equipeMontarDTO.getIdsInteressesSelecionados(),
                equipeMontarDTO.getNomeEquipeSugerido(),
                empresaId
        );
        return ResponseEntity.ok(equipeMontada);
    }

    @GetMapping
    public ResponseEntity<EquipeProjetoResponseDTO> visualizarEquipeProjeto(@PathVariable Long projetoId) {
        EquipeProjetoResponseDTO equipe = equipeProjetoService.visualizarEquipeProjeto(projetoId);
        return ResponseEntity.ok(equipe);
    }
}