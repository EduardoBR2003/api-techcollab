package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.ProfissionalCreateDTO;
import br.com.api_techcollab.dto.ProfissionalResponseDTO;
import br.com.api_techcollab.services.InteresseProjetoService;
import br.com.api_techcollab.services.ProfissionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profissionais")
@Tag(name = "Profissionais", description = "Endpoints para o gerenciamento de perfis de profissionais.")
public class ProfissionalController {

    @Autowired
    private ProfissionalService profissionalService;

    @GetMapping
    @Operation(summary = "Listar todos os profissionais", description = "Retorna uma lista de todos os profissionais cadastrados.", tags = {"Profissionais"})
    @ApiResponse(responseCode = "200", description = "Lista de profissionais retornada com sucesso.")
    public ResponseEntity<List<ProfissionalResponseDTO>> findAll() {
        List<ProfissionalResponseDTO> profissionais = profissionalService.findAll();
        return ResponseEntity.ok(profissionais);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar profissional por ID", description = "Retorna os detalhes de um profissional específico pelo seu ID.", tags = {"Profissionais"})
    @ApiResponse(responseCode = "200", description = "Profissional encontrado.", content = @Content(schema = @Schema(implementation = ProfissionalResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado.")
    public ResponseEntity<ProfissionalResponseDTO> findById(@Parameter(description = "ID do profissional a ser buscado") @PathVariable Long id) {
        ProfissionalResponseDTO profissional = profissionalService.findById(id);
        return ResponseEntity.ok(profissional);
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo profissional", description = "[RF012] Realiza o cadastro de um novo profissional na plataforma.", tags = {"Profissionais"})
    @ApiResponse(responseCode = "201", description = "Profissional criado com sucesso.", content = @Content(schema = @Schema(implementation = ProfissionalResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos (ex: email já existe).")
    public ResponseEntity<ProfissionalResponseDTO> create(@RequestBody ProfissionalCreateDTO dto) {
        ProfissionalResponseDTO createdProfissional = profissionalService.create(dto);
        return new ResponseEntity<>(createdProfissional, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um profissional", description = "[RF012] Atualiza os dados de um profissional existente.", tags = {"Profissionais"})
    @ApiResponse(responseCode = "200", description = "Profissional atualizado com sucesso.", content = @Content(schema = @Schema(implementation = ProfissionalResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado.")
    public ResponseEntity<ProfissionalResponseDTO> update(@Parameter(description = "ID do profissional a ser atualizado") @PathVariable Long id, @RequestBody ProfissionalCreateDTO dto) {
        ProfissionalResponseDTO updatedProfissional = profissionalService.update(id, dto);
        return ResponseEntity.ok(updatedProfissional);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um profissional", description = "Remove o registro de um profissional. Apenas permitido se o profissional não tiver interesses ativos ou participação em equipes.", tags = {"Profissionais"})
    @ApiResponse(responseCode = "204", description = "Profissional excluído com sucesso.")
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado.")
    @ApiResponse(responseCode = "400", description = "Profissional não pode ser excluído por ter dependências ativas.")
    public ResponseEntity<Void> delete(@Parameter(description = "ID do profissional a ser excluído") @PathVariable Long id) {
        profissionalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}