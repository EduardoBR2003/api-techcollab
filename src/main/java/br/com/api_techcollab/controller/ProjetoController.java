package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.ProjetoCreateDTO;
import br.com.api_techcollab.dto.ProjetoResponseDTO;
import br.com.api_techcollab.dto.ProjetoUpdateDTO;
import br.com.api_techcollab.services.ProjetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    @Autowired
    private ProjetoService projetoService;

    @GetMapping("/disponiveis")
    public ResponseEntity<List<ProjetoResponseDTO>> consultarProjetosDisponiveis() {
        List<ProjetoResponseDTO> projetos = projetoService.consultarProjetosDisponiveis();
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/{projetoId}")
    public ResponseEntity<ProjetoResponseDTO> buscarProjetoPorId(@PathVariable Long projetoId) {
        ProjetoResponseDTO projeto = projetoService.buscarProjetoPorId(projetoId);
        return ResponseEntity.ok(projeto);
    }

    // [RF004] Editar Projeto - Refatorado
    @PutMapping("/{projetoId}")
    public ResponseEntity<ProjetoResponseDTO> editarProjeto(
            @PathVariable Long projetoId,
            @RequestBody ProjetoUpdateDTO projetoUpdateDTO) {
        // A validação do ID da empresa agora é feita com o ID enviado no DTO
        ProjetoResponseDTO projetoAtualizado = projetoService.editarProjeto(projetoId, projetoUpdateDTO, projetoUpdateDTO.getEmpresaId());
        return ResponseEntity.ok(projetoAtualizado);
    }

    // [RF004] Excluir Projeto - Refatorado
    @DeleteMapping("/{projetoId}")
    public ResponseEntity<Void> excluirProjeto(
            @PathVariable Long projetoId,
            @RequestParam Long empresaId) { // ID da empresa vem como parâmetro de consulta
        projetoService.excluirProjeto(projetoId, empresaId);
        return ResponseEntity.noContent().build();
    }
}