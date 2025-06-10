package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.VagaProjetoCreateDTO;
import br.com.api_techcollab.dto.VagaProjetoResponseDTO;
import br.com.api_techcollab.services.VagaProjetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos/{projetoId}/vagas")
public class VagaProjetoController {

    @Autowired
    private VagaProjetoService vagaProjetoService;

    @PostMapping
    public ResponseEntity<VagaProjetoResponseDTO> criarVaga(
            @PathVariable Long projetoId,
            @RequestBody VagaProjetoCreateDTO vagaCreateDTO) {
        VagaProjetoResponseDTO novaVaga = vagaProjetoService.criarVagaParaProjeto(projetoId, vagaCreateDTO);
        return new ResponseEntity<>(novaVaga, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VagaProjetoResponseDTO>> listarVagasPorProjeto(@PathVariable Long projetoId) {
        List<VagaProjetoResponseDTO> vagas = vagaProjetoService.listarVagasPorProjeto(projetoId);
        return ResponseEntity.ok(vagas);
    }

    @PutMapping("/{vagaId}")
    public ResponseEntity<VagaProjetoResponseDTO> editarVaga(
            @PathVariable Long vagaId,
            @RequestBody VagaProjetoCreateDTO vagaCreateDTO) {
        VagaProjetoResponseDTO vagaAtualizada = vagaProjetoService.editarVagaProjeto(vagaId, vagaCreateDTO);
        return ResponseEntity.ok(vagaAtualizada);
    }

    // Excluir uma Vaga - Agora com empresaId para validação
    @DeleteMapping("/{vagaId}/empresa/{empresaId}")
    public ResponseEntity<Void> excluirVaga(
            @PathVariable Long vagaId,
            @PathVariable Long empresaId) {
        vagaProjetoService.excluirVagaProjeto(vagaId, empresaId);
        return ResponseEntity.noContent().build();
    }
}