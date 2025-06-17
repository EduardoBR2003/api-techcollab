package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.EmpresaCreateDTO;
import br.com.api_techcollab.dto.EmpresaResponseDTO;
import br.com.api_techcollab.dto.ProjetoCreateDTO;
import br.com.api_techcollab.dto.ProjetoResponseDTO;
import br.com.api_techcollab.services.EmpresaService;
import br.com.api_techcollab.services.ProjetoService;
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
@RequestMapping("/api/empresas")
@Tag(name = "Empresas", description = "Endpoints para o gerenciamento de perfis de empresas e seus projetos.")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private ProjetoService projetoService;

    @GetMapping
    @Operation(summary = "Listar todas as empresas", description = "Retorna uma lista de todas as empresas cadastradas na plataforma.", tags = {"Empresas"})
    @ApiResponse(responseCode = "200", description = "Lista de empresas retornada com sucesso.")
    public ResponseEntity<List<EmpresaResponseDTO>> findAll() {
        List<EmpresaResponseDTO> empresas = empresaService.findAll();
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar empresa por ID", description = "Retorna os detalhes de uma empresa específica pelo seu ID.", tags = {"Empresas"})
    @ApiResponse(responseCode = "200", description = "Empresa encontrada.", content = @Content(schema = @Schema(implementation = EmpresaResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Empresa não encontrada.")
    public ResponseEntity<EmpresaResponseDTO> findById(@Parameter(description = "ID da empresa a ser buscada") @PathVariable Long id) {
        EmpresaResponseDTO empresa = empresaService.findById(id);
        return ResponseEntity.ok(empresa);
    }

    @PostMapping
    @Operation(summary = "Cadastrar uma nova empresa", description = "[RF012] Realiza o cadastro de uma nova empresa na plataforma.", tags = {"Empresas"})
    @ApiResponse(responseCode = "201", description = "Empresa criada com sucesso.", content = @Content(schema = @Schema(implementation = EmpresaResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos (ex: email ou CNPJ já existe).")
    public ResponseEntity<EmpresaResponseDTO> create(@RequestBody EmpresaCreateDTO dto) {
        EmpresaResponseDTO createdEmpresa = empresaService.create(dto);
        return new ResponseEntity<>(createdEmpresa, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma empresa", description = "[RF012] Atualiza os dados de uma empresa existente.", tags = {"Empresas"})
    @ApiResponse(responseCode = "200", description = "Empresa atualizada com sucesso.", content = @Content(schema = @Schema(implementation = EmpresaResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Empresa não encontrada.")
    public ResponseEntity<EmpresaResponseDTO> update(@Parameter(description = "ID da empresa a ser atualizada") @PathVariable Long id, @RequestBody EmpresaCreateDTO dto) {
        EmpresaResponseDTO updatedEmpresa = empresaService.update(id, dto);
        return ResponseEntity.ok(updatedEmpresa);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma empresa", description = "Remove o registro de uma empresa. Apenas permitido se a empresa não tiver projetos associados.", tags = {"Empresas"})
    @ApiResponse(responseCode = "204", description = "Empresa excluída com sucesso.")
    @ApiResponse(responseCode = "404", description = "Empresa não encontrada.")
    @ApiResponse(responseCode = "400", description = "Empresa não pode ser excluída por ter projetos.")
    public ResponseEntity<Void> delete(@Parameter(description = "ID da empresa a ser excluída") @PathVariable Long id) {
        empresaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{empresaId}/projetos")
    @Operation(summary = "Criar um novo projeto para uma empresa", description = "[RF003] Permite que uma empresa crie um novo projeto, associando-o ao seu perfil.", tags = {"Empresas"})
    @ApiResponse(responseCode = "201", description = "Projeto criado com sucesso.", content = @Content(schema = @Schema(implementation = ProjetoResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Empresa não encontrada.")
    public ResponseEntity<ProjetoResponseDTO> criarProjetoDaEmpresa(@Parameter(description = "ID da empresa proprietária do projeto") @PathVariable Long empresaId, @RequestBody ProjetoCreateDTO projetoCreateDTO) {
        ProjetoResponseDTO novoProjeto = projetoService.criarProjeto(projetoCreateDTO, empresaId);
        return new ResponseEntity<>(novoProjeto, HttpStatus.CREATED);
    }

    @GetMapping("/{empresaId}/projetos")
    @Operation(summary = "Listar projetos de uma empresa", description = "Retorna a lista de todos os projetos pertencentes a uma empresa específica.", tags = {"Empresas"})
    @ApiResponse(responseCode = "200", description = "Lista de projetos retornada com sucesso.")
    @ApiResponse(responseCode = "404", description = "Empresa não encontrada.")
    public ResponseEntity<List<ProjetoResponseDTO>> consultarProjetosPorEmpresa(@Parameter(description = "ID da empresa para consultar os projetos") @PathVariable Long empresaId) {
        List<ProjetoResponseDTO> projetos = projetoService.consultarProjetosPorEmpresa(empresaId);
        return ResponseEntity.ok(projetos);
    }
}