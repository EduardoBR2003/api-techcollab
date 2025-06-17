package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.ProjetoResponseDTO;
import br.com.api_techcollab.dto.ProjetoUpdateDTO;
import br.com.api_techcollab.services.ProjetoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
@Tag(name = "Projetos", description = "Endpoints para ações gerais sobre projetos.")
public class ProjetoController {

    @Autowired
    private ProjetoService projetoService;

    @GetMapping("/disponiveis")
    @Operation(summary = "Listar projetos disponíveis", description = "[RF005] Retorna todos os projetos que estão com o status 'ABERTO_PARA_INTERESSE'.", tags = {"Projetos"})
    @ApiResponse(responseCode = "200", description = "Lista de projetos disponíveis retornada com sucesso.")
    public ResponseEntity<List<ProjetoResponseDTO>> consultarProjetosDisponiveis() {
        List<ProjetoResponseDTO> projetos = projetoService.consultarProjetosDisponiveis();
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/{projetoId}")
    @Operation(summary = "Buscar projeto por ID", description = "Retorna os detalhes de um projeto específico.", tags = {"Projetos"})
    @ApiResponse(responseCode = "200", description = "Projeto encontrado.", content = @Content(schema = @Schema(implementation = ProjetoResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado.")
    public ResponseEntity<ProjetoResponseDTO> buscarProjetoPorId(@Parameter(description = "ID do projeto a ser buscado") @PathVariable Long projetoId) {
        ProjetoResponseDTO projeto = projetoService.buscarProjetoPorId(projetoId);
        return ResponseEntity.ok(projeto);
    }

    @PutMapping("/{projetoId}")
    @Operation(summary = "Editar um projeto", description = "[RF004] Permite que a empresa proprietária edite os detalhes de um projeto. O ID da empresa deve ser enviado no corpo da requisição.", tags = {"Projetos"})
    @ApiResponse(responseCode = "200", description = "Projeto atualizado com sucesso.", content = @Content(schema = @Schema(implementation = ProjetoResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado. A empresa não é proprietária do projeto.")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado.")
    public ResponseEntity<ProjetoResponseDTO> editarProjeto(
            @Parameter(description = "ID do projeto a ser editado") @PathVariable Long projetoId,
            @RequestBody ProjetoUpdateDTO projetoUpdateDTO) {
        ProjetoResponseDTO projetoAtualizado = projetoService.editarProjeto(projetoId, projetoUpdateDTO, projetoUpdateDTO.getEmpresaId());
        return ResponseEntity.ok(projetoAtualizado);
    }

    @DeleteMapping("/{projetoId}")
    @Operation(summary = "Excluir um projeto", description = "[RF004] Permite que a empresa proprietária exclua um projeto. Apenas projetos em status inicial podem ser excluídos.", tags = {"Projetos"})
    @ApiResponse(responseCode = "204", description = "Projeto excluído com sucesso.")
    @ApiResponse(responseCode = "403", description = "Acesso negado. A empresa não é proprietária do projeto.")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado.")
    @ApiResponse(responseCode = "400", description = "Projeto não pode ser excluído devido ao seu status atual.")
    public ResponseEntity<Void> excluirProjeto(
            @Parameter(description = "ID do projeto a ser excluído") @PathVariable Long projetoId,
            @Parameter(description = "ID da empresa proprietária que está realizando a exclusão") @RequestParam Long empresaId) {
        projetoService.excluirProjeto(projetoId, empresaId);
        return ResponseEntity.noContent().build();
    }
}