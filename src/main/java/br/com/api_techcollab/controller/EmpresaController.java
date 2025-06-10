package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.EmpresaCreateDTO;
import br.com.api_techcollab.dto.EmpresaResponseDTO;
import br.com.api_techcollab.dto.ProjetoCreateDTO;
import br.com.api_techcollab.dto.ProjetoResponseDTO;
import br.com.api_techcollab.services.EmpresaService;
import br.com.api_techcollab.services.ProjetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private ProjetoService projetoService;

    @GetMapping
    public ResponseEntity<List<EmpresaResponseDTO>> findAll() {
        List<EmpresaResponseDTO> empresas = empresaService.findAll();
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> findById(@PathVariable Long id) {
        EmpresaResponseDTO empresa = empresaService.findById(id);
        return ResponseEntity.ok(empresa);
    }

    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> create(@RequestBody EmpresaCreateDTO dto) {
        EmpresaResponseDTO createdEmpresa = empresaService.create(dto);
        return new ResponseEntity<>(createdEmpresa, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> update(@PathVariable Long id, @RequestBody EmpresaCreateDTO dto) {
        EmpresaResponseDTO updatedEmpresa = empresaService.update(id, dto);
        return ResponseEntity.ok(updatedEmpresa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        empresaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Rotas de Projetos aninhadas sob Empresa ---

    // [RF003] Criar Projeto (associado a esta empresa)
    @PostMapping("/{empresaId}/projetos")
    public ResponseEntity<ProjetoResponseDTO> criarProjetoDaEmpresa(@PathVariable Long empresaId, @RequestBody ProjetoCreateDTO projetoCreateDTO) {
        ProjetoResponseDTO novoProjeto = projetoService.criarProjeto(projetoCreateDTO, empresaId);
        return new ResponseEntity<>(novoProjeto, HttpStatus.CREATED);
    }

    // Visualizar Projetos de uma Empresa específica
    @GetMapping("/{empresaId}/projetos")
    public ResponseEntity<List<ProjetoResponseDTO>> consultarProjetosPorEmpresa(@PathVariable Long empresaId) {
        List<ProjetoResponseDTO> projetos = projetoService.consultarProjetosPorEmpresa(empresaId);
        return ResponseEntity.ok(projetos);
    }
}