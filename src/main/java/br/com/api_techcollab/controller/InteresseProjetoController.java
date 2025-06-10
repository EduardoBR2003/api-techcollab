package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.InteresseCreateDTO;
import br.com.api_techcollab.dto.InteresseProjetoResponseDTO;
import br.com.api_techcollab.dto.InteresseStatusUpdateDTO;
import br.com.api_techcollab.services.InteresseProjetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InteresseProjetoController {

    @Autowired
    private InteresseProjetoService interesseProjetoService;

    // --- Perspectiva da Empresa ---

    @GetMapping("/empresas/{empresaId}/vagas/{vagaId}/interesses")
    public ResponseEntity<List<InteresseProjetoResponseDTO>> visualizarInteressadosPorVaga(
            @PathVariable Long vagaId,
            @PathVariable Long empresaId) {
        List<InteresseProjetoResponseDTO> interessados = interesseProjetoService.visualizarInteressadosPorVaga(vagaId, empresaId);
        return ResponseEntity.ok(interessados);
    }

    @PutMapping("/empresas/{empresaId}/interesses/{interesseId}")
    public ResponseEntity<InteresseProjetoResponseDTO> atualizarStatusInteresseEmpresa(
            @PathVariable Long interesseId,
            @PathVariable Long empresaId,
            @RequestBody InteresseStatusUpdateDTO statusUpdateDTO) {
        InteresseProjetoResponseDTO interesseAtualizado = interesseProjetoService.atualizarStatusInteresseEmpresa(interesseId, statusUpdateDTO, empresaId);
        return ResponseEntity.ok(interesseAtualizado);
    }

    // --- Perspectiva do Profissional ---

    @PostMapping("/profissionais/{profissionalId}/interesses")
    public ResponseEntity<InteresseProjetoResponseDTO> manifestarInteresse(
            @PathVariable Long profissionalId,
            @RequestBody InteresseCreateDTO interesseCreateDTO) {
        InteresseProjetoResponseDTO novoInteresse = interesseProjetoService.manifestarInteresse(profissionalId, interesseCreateDTO);
        return new ResponseEntity<>(novoInteresse, HttpStatus.CREATED);
    }

    @PutMapping("/profissionais/{profissionalId}/interesses/{interesseId}")
    public ResponseEntity<InteresseProjetoResponseDTO> profissionalResponderAlocacao(
            @PathVariable Long profissionalId,
            @PathVariable Long interesseId,
            @RequestBody InteresseStatusUpdateDTO statusUpdateDTO) {
        InteresseProjetoResponseDTO interesseAtualizado = interesseProjetoService.profissionalResponderAlocacao(interesseId, statusUpdateDTO, profissionalId);
        return ResponseEntity.ok(interesseAtualizado);
    }

    @GetMapping("/profissionais/{profissionalId}/interesses")
    public ResponseEntity<List<InteresseProjetoResponseDTO>> consultarStatusInteressesProfissional(@PathVariable Long profissionalId) {
        List<InteresseProjetoResponseDTO> interesses = interesseProjetoService.consultarStatusInteressesProfissional(profissionalId);
        return ResponseEntity.ok(interesses);
    }
}