package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.EquipeMontarDTO;
import br.com.api_techcollab.dto.EquipeProjetoResponseDTO;
import br.com.api_techcollab.services.EquipeProjetoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projetos/{projetoId}/equipe")
@Tag(name = "Equipes", description = "Endpoints para montagem e visualização da equipe de um projeto.")
public class EquipeProjetoController {

    @Autowired
    private EquipeProjetoService equipeProjetoService;

    @PostMapping
    @Operation(summary = "Montar a equipe de um projeto", description = "[RF008] A empresa seleciona os profissionais (a partir de seus interesses com status 'SELECIONADO') para formar a equipe de um projeto. Os interesses selecionados terão seu status alterado para 'ALOCADO'.", tags = {"Equipes"})
    @ApiResponse(responseCode = "200", description = "Equipe montada/atualizada com sucesso.")
    @ApiResponse(responseCode = "404", description = "Projeto ou interesse não encontrado.")
    @ApiResponse(responseCode = "403", description = "Acesso negado. A empresa não é proprietária do projeto.")
    @ApiResponse(responseCode = "400", description = "Operação inválida para o status atual do projeto ou interesse.")
    public ResponseEntity<EquipeProjetoResponseDTO> montarEquipe(
            @Parameter(description = "ID do projeto para o qual a equipe será montada") @PathVariable Long projetoId,
            @RequestBody EquipeMontarDTO equipeMontarDTO) {
        EquipeProjetoResponseDTO equipeMontada = equipeProjetoService.montarEquipe(
                projetoId,
                equipeMontarDTO.getIdsInteressesSelecionados(),
                equipeMontarDTO.getNomeEquipeSugerido(),
                equipeMontarDTO.getEmpresaId()
        );
        return ResponseEntity.ok(equipeMontada);
    }

    @GetMapping
    @Operation(summary = "Visualizar a equipe de um projeto", description = "[RF010] Retorna os detalhes da equipe formada para um projeto, incluindo seus membros e papéis.", tags = {"Equipes"})
    @ApiResponse(responseCode = "200", description = "Equipe retornada com sucesso.")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado.")
    public ResponseEntity<EquipeProjetoResponseDTO> visualizarEquipeProjeto(@Parameter(description = "ID do projeto para visualizar a equipe") @PathVariable Long projetoId) {
        EquipeProjetoResponseDTO equipe = equipeProjetoService.visualizarEquipeProjeto(projetoId);
        return ResponseEntity.ok(equipe);
    }
}