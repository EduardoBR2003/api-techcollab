package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.InteresseCreateDTO;
import br.com.api_techcollab.dto.InteresseProjetoResponseDTO;
import br.com.api_techcollab.dto.InteresseStatusUpdateDTO;
import br.com.api_techcollab.services.InteresseProjetoService;
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
@RequestMapping("/api")
@Tag(name = "Interesses (Candidaturas)", description = "Endpoints para gerenciar os interesses de profissionais em vagas e as ações da empresa sobre eles.")
public class InteresseProjetoController {

    @Autowired
    private InteresseProjetoService interesseProjetoService;

    @GetMapping("/empresas/{empresaId}/vagas/{vagaId}/interesses")
    @Operation(summary = "Visualizar interessados em uma vaga", description = "[RF007] Permite que a empresa visualize a lista de profissionais que manifestaram interesse em uma de suas vagas.", tags = {"Interesses (Candidaturas)"})
    @ApiResponse(responseCode = "200", description = "Lista de interessados retornada com sucesso.")
    @ApiResponse(responseCode = "403", description = "Acesso negado. A empresa não é proprietária da vaga.")
    public ResponseEntity<List<InteresseProjetoResponseDTO>> visualizarInteressadosPorVaga(
            @Parameter(description = "ID da vaga para listar os interesses") @PathVariable Long vagaId,
            @Parameter(description = "ID da empresa proprietária") @PathVariable Long empresaId) {
        List<InteresseProjetoResponseDTO> interessados = interesseProjetoService.visualizarInteressadosPorVaga(vagaId, empresaId);
        return ResponseEntity.ok(interessados);
    }

    @PutMapping("/empresas/{empresaId}/interesses/{interesseId}")
    @Operation(summary = "Empresa atualiza status de um interesse", description = "Permite que a empresa altere o status de um interesse para 'SELECIONADO' ou 'RECUSADO_PELA_EMPRESA'.", tags = {"Interesses (Candidaturas)"})
    @ApiResponse(responseCode = "200", description = "Status do interesse atualizado com sucesso.")
    @ApiResponse(responseCode = "400", description = "Atualização de status inválida.")
    @ApiResponse(responseCode = "403", description = "Acesso negado.")
    public ResponseEntity<InteresseProjetoResponseDTO> atualizarStatusInteresseEmpresa(
            @Parameter(description = "ID do interesse a ser atualizado") @PathVariable Long interesseId,
            @Parameter(description = "ID da empresa que está atualizando") @PathVariable Long empresaId,
            @RequestBody InteresseStatusUpdateDTO statusUpdateDTO) {
        InteresseProjetoResponseDTO interesseAtualizado = interesseProjetoService.atualizarStatusInteresseEmpresa(interesseId, statusUpdateDTO, empresaId);
        return ResponseEntity.ok(interesseAtualizado);
    }

    @PostMapping("/profissionais/{profissionalId}/interesses")
    @Operation(summary = "Profissional manifesta interesse em uma vaga", description = "[RF006] Permite que um profissional se candidate a uma vaga de um projeto.", tags = {"Interesses (Candidaturas)"})
    @ApiResponse(responseCode = "201", description = "Interesse manifestado com sucesso.")
    @ApiResponse(responseCode = "400", description = "Profissional já manifestou interesse nesta vaga ou projeto não está aberto.")
    @ApiResponse(responseCode = "404", description = "Profissional ou vaga não encontrado.")
    public ResponseEntity<InteresseProjetoResponseDTO> manifestarInteresse(
            @Parameter(description = "ID do profissional que está manifestando interesse") @PathVariable Long profissionalId,
            @RequestBody InteresseCreateDTO interesseCreateDTO) {
        InteresseProjetoResponseDTO novoInteresse = interesseProjetoService.manifestarInteresse(profissionalId, interesseCreateDTO);
        return new ResponseEntity<>(novoInteresse, HttpStatus.CREATED);
    }

    @PutMapping("/profissionais/{profissionalId}/interesses/{interesseId}")
    @Operation(summary = "Profissional aceita ou recusa alocação", description = "[RF009] Permite que um profissional, cujo interesse foi 'SELECIONADO' pela empresa, aceite ('ALOCADO') ou recuse ('RECUSADO_DO_PROF') a participação na equipe.", tags = {"Interesses (Candidaturas)"})
    @ApiResponse(responseCode = "200", description = "Resposta registrada com sucesso.")
    @ApiResponse(responseCode = "400", description = "Ação inválida para o status atual do interesse.")
    @ApiResponse(responseCode = "403", description = "Acesso negado.")
    public ResponseEntity<InteresseProjetoResponseDTO> profissionalResponderAlocacao(
            @Parameter(description = "ID do profissional que está respondendo") @PathVariable Long profissionalId,
            @Parameter(description = "ID do interesse a ser respondido") @PathVariable Long interesseId,
            @RequestBody InteresseStatusUpdateDTO statusUpdateDTO) {
        InteresseProjetoResponseDTO interesseAtualizado = interesseProjetoService.profissionalResponderAlocacao(interesseId, statusUpdateDTO, profissionalId);
        return ResponseEntity.ok(interesseAtualizado);
    }

    @GetMapping("/profissionais/{profissionalId}/interesses")
    @Operation(summary = "Profissional consulta seus interesses", description = "[RF011] Retorna a lista de todos os interesses (candidaturas) de um profissional e seus respectivos status.", tags = {"Interesses (Candidaturas)"})
    @ApiResponse(responseCode = "200", description = "Lista de interesses retornada com sucesso.")
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado.")
    public ResponseEntity<List<InteresseProjetoResponseDTO>> consultarStatusInteressesProfissional(@Parameter(description = "ID do profissional para consultar os interesses") @PathVariable Long profissionalId) {
        List<InteresseProjetoResponseDTO> interesses = interesseProjetoService.consultarStatusInteressesProfissional(profissionalId);
        return ResponseEntity.ok(interesses);
    }
}