package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.VagaProjetoCreateDTO;
import br.com.api_techcollab.dto.VagaProjetoResponseDTO;
import br.com.api_techcollab.services.VagaProjetoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos/{projetoId}/vagas")
@Tag(name = "Vagas", description = "Endpoints para o gerenciamento de vagas dentro de um projeto.")
public class VagaProjetoController {

    @Autowired
    private VagaProjetoService vagaProjetoService;

    @PostMapping
    @Operation(summary = "Criar uma nova vaga para um projeto", description = "Permite que a empresa crie uma nova vaga, especificando habilidades e nível de experiência, dentro de um de seus projetos.", tags = {"Vagas"})
    @ApiResponse(responseCode = "201", description = "Vaga criada com sucesso.")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado.")
    @ApiResponse(responseCode = "400", description = "Não é possível adicionar vaga ao projeto no status atual.")
    public ResponseEntity<VagaProjetoResponseDTO> criarVaga(
            @Parameter(description = "ID do projeto ao qual a vaga pertence") @PathVariable Long projetoId,
            @RequestBody VagaProjetoCreateDTO vagaCreateDTO) {
        VagaProjetoResponseDTO novaVaga = vagaProjetoService.criarVagaParaProjeto(projetoId, vagaCreateDTO);
        return new ResponseEntity<>(novaVaga, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todas as vagas de um projeto", description = "Retorna a lista de todas as vagas abertas para um projeto específico.", tags = {"Vagas"})
    @ApiResponse(responseCode = "200", description = "Lista de vagas retornada com sucesso.")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado.")
    public ResponseEntity<List<VagaProjetoResponseDTO>> listarVagasPorProjeto(@Parameter(description = "ID do projeto para consultar as vagas") @PathVariable Long projetoId) {
        List<VagaProjetoResponseDTO> vagas = vagaProjetoService.listarVagasPorProjeto(projetoId);
        return ResponseEntity.ok(vagas);
    }

    @GetMapping("/{vagaId}")
    @Operation(summary = "Buscar uma vaga específica", description = "Retorna os detalhes de uma vaga específica pelo seu ID.", tags = {"Vagas"})
    @ApiResponse(responseCode = "200", description = "Vaga encontrada.")
    @ApiResponse(responseCode = "404", description = "Vaga ou projeto não encontrado.")
    public ResponseEntity<VagaProjetoResponseDTO> getVaga(
            @Parameter(description = "ID do projeto") @PathVariable Long projetoId,
            @Parameter(description = "ID da vaga a ser buscada") @PathVariable Long vagaId) {
        VagaProjetoResponseDTO vaga = vagaProjetoService.buscarVagaPorId(vagaId);
        return ResponseEntity.ok(vaga);
    }

    @PutMapping("/{vagaId}")
    @Operation(summary = "Editar uma vaga", description = "Permite que a empresa edite os detalhes de uma vaga existente. Não é permitido se a vaga já tiver interesses ativos.", tags = {"Vagas"})
    @ApiResponse(responseCode = "200", description = "Vaga atualizada com sucesso.")
    @ApiResponse(responseCode = "404", description = "Vaga ou projeto não encontrado.")
    @ApiResponse(responseCode = "400", description = "Vaga não pode ser editada por ter dependências.")
    public ResponseEntity<VagaProjetoResponseDTO> editarVaga(
            @Parameter(description = "ID do projeto") @PathVariable Long projetoId,
            @Parameter(description = "ID da vaga a ser editada") @PathVariable Long vagaId,
            @RequestBody VagaProjetoCreateDTO vagaCreateDTO) {
        VagaProjetoResponseDTO vagaAtualizada = vagaProjetoService.editarVagaProjeto(vagaId, vagaCreateDTO);
        return ResponseEntity.ok(vagaAtualizada);
    }

    @DeleteMapping("/{vagaId}")
    @Operation(summary = "Excluir uma vaga", description = "Permite que a empresa proprietária exclua uma vaga. Não é permitido se a vaga já possuir interesses.", tags = {"Vagas"})
    @ApiResponse(responseCode = "204", description = "Vaga excluída com sucesso.")
    @ApiResponse(responseCode = "403", description = "Acesso negado. A empresa não é proprietária do projeto.")
    @ApiResponse(responseCode = "404", description = "Vaga ou projeto não encontrado.")
    @ApiResponse(responseCode = "400", description = "Vaga não pode ser excluída por ter interesses associados.")
    public ResponseEntity<Void> excluirVaga(
            @Parameter(description = "ID do projeto") @PathVariable Long projetoId,
            @Parameter(description = "ID da vaga a ser excluída") @PathVariable Long vagaId,
            @Parameter(description = "ID da empresa proprietária que está realizando a exclusão") @RequestParam Long empresaId) {
        vagaProjetoService.excluirVagaProjeto(vagaId, empresaId);
        return ResponseEntity.noContent().build();
    }
}