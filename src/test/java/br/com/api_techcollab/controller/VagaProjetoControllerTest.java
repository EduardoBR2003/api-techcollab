package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.VagaProjetoCreateDTO;
import br.com.api_techcollab.dto.VagaProjetoResponseDTO;
import br.com.api_techcollab.services.VagaProjetoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class VagaProjetoControllerTest {

    private MockMvc mockMvc;

    @Mock // Cria um mock do VagaProjetoService.
    private VagaProjetoService vagaProjetoService;

    @InjectMocks // Injeta o mock no VagaProjetoController.
    private VagaProjetoController vagaProjetoController;

    private ObjectMapper objectMapper;

    private VagaProjetoResponseDTO vagaProjetoResponseDTO;
    private VagaProjetoCreateDTO vagaProjetoCreateDTO;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        objectMapper = new ObjectMapper();
        // Configura o MockMvc para testar o Controller isoladamente.
        mockMvc = MockMvcBuilders.standaloneSetup(vagaProjetoController).build();

        // Inicializa os DTOs de resposta e criação para serem usados nos testes.
        vagaProjetoResponseDTO = new VagaProjetoResponseDTO();
        vagaProjetoResponseDTO.setId(1L);
        vagaProjetoResponseDTO.setTituloVaga("Desenvolvedor Teste");
        vagaProjetoResponseDTO.setProjetoId(10L);

        vagaProjetoCreateDTO = new VagaProjetoCreateDTO();
        vagaProjetoCreateDTO.setTituloVaga("Nova Vaga");
    }

    @Test // Marca o método como um teste.
    @DisplayName("Deve criar uma vaga para um projeto") // Nome amigável para o teste.
    void criarVaga_Success() throws Exception {
        Long projetoId = 10L;
        // Configura o mock do serviço para retornar o DTO de resposta quando criarVagaParaProjeto for chamado.
        when(vagaProjetoService.criarVagaParaProjeto(eq(projetoId), any(VagaProjetoCreateDTO.class)))
                .thenReturn(vagaProjetoResponseDTO);

        // Realiza uma requisição POST para o endpoint de criação de vaga.
        mockMvc.perform(post("/api/projetos/{projetoId}/vagas", projetoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vagaProjetoCreateDTO)))
                // Espera um status HTTP 201 CREATED e verifica o ID da vaga criada.
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(vagaProjetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve listar vagas por projeto")
    void listarVagasPorProjeto_Success() throws Exception {
        Long projetoId = 10L;
        // Configura o mock do serviço para retornar uma lista de vagas quando listarVagasPorProjeto for chamado.
        List<VagaProjetoResponseDTO> listaVagas = Collections.singletonList(vagaProjetoResponseDTO);
        when(vagaProjetoService.listarVagasPorProjeto(projetoId)).thenReturn(listaVagas);

        // Realiza uma requisição GET para o endpoint de listagem de vagas por projeto.
        mockMvc.perform(get("/api/projetos/{projetoId}/vagas", projetoId)
                        .contentType(MediaType.APPLICATION_JSON))
                // Espera um status HTTP 200 OK e verifica o ID da primeira vaga na lista.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(vagaProjetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve retornar vaga por ID")
    void getVaga_Success() throws Exception {
        Long projetoId = 10L;
        Long vagaId = 1L;
        // Configura o mock do serviço para retornar o DTO de resposta quando buscarVagaPorId for chamado.
        when(vagaProjetoService.buscarVagaPorId(vagaId)).thenReturn(vagaProjetoResponseDTO);

        // Realiza uma requisição GET para o endpoint de obtenção de vaga por ID.
        mockMvc.perform(get("/api/projetos/{projetoId}/vagas/{vagaId}", projetoId, vagaId)
                        .contentType(MediaType.APPLICATION_JSON))
                // Espera um status HTTP 200 OK e verifica o ID da vaga.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vagaProjetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve editar uma vaga existente")
    void editarVaga_Success() throws Exception {
        Long projetoId = 10L;
        Long vagaId = 1L;
        // Configura o mock do serviço para retornar o DTO de resposta quando editarVagaProjeto for chamado.
        when(vagaProjetoService.editarVagaProjeto(eq(vagaId), any(VagaProjetoCreateDTO.class)))
                .thenReturn(vagaProjetoResponseDTO);

        // Realiza uma requisição PUT para o endpoint de edição de vaga.
        mockMvc.perform(put("/api/projetos/{projetoId}/vagas/{vagaId}", projetoId, vagaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vagaProjetoCreateDTO)))
                // Espera um status HTTP 200 OK e verifica o ID da vaga atualizada.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vagaProjetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve excluir uma vaga")
    void excluirVaga_Success() throws Exception {
        Long projetoId = 10L;
        Long vagaId = 1L;
        Long empresaId = 1L; // ID da empresa para a requisição
        // Configura o mock do serviço para não fazer nada quando excluirVagaProjeto for chamado.
        doNothing().when(vagaProjetoService).excluirVagaProjeto(vagaId, empresaId);

        // Realiza uma requisição DELETE para o endpoint de exclusão de vaga.
        mockMvc.perform(delete("/api/projetos/{projetoId}/vagas/{vagaId}", projetoId, vagaId)
                        .param("empresaId", empresaId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                // Espera um status HTTP 204 No Content.
                .andExpect(status().isNoContent());
    }
}