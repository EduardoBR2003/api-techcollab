// src/test/java/br/com/api_techcollab/controller/VagaProjetoControllerTest.java
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

    @Mock
    private VagaProjetoService vagaProjetoService;

    @InjectMocks
    private VagaProjetoController vagaProjetoController;

    private ObjectMapper objectMapper;

    private VagaProjetoResponseDTO vagaProjetoResponseDTO;
    private VagaProjetoCreateDTO vagaProjetoCreateDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(vagaProjetoController).build();

        vagaProjetoResponseDTO = new VagaProjetoResponseDTO();
        vagaProjetoResponseDTO.setId(1L);
        vagaProjetoResponseDTO.setTituloVaga("Desenvolvedor Teste");
        vagaProjetoResponseDTO.setProjetoId(10L);

        vagaProjetoCreateDTO = new VagaProjetoCreateDTO();
        vagaProjetoCreateDTO.setTituloVaga("Nova Vaga");
    }

    @Test
    @DisplayName("Deve criar uma vaga para um projeto")
    void criarVaga_Success() throws Exception {
        Long projetoId = 10L;
        when(vagaProjetoService.criarVagaParaProjeto(eq(projetoId), any(VagaProjetoCreateDTO.class)))
                .thenReturn(vagaProjetoResponseDTO);

        mockMvc.perform(post("/api/projetos/{projetoId}/vagas", projetoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vagaProjetoCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(vagaProjetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve listar vagas por projeto")
    void listarVagasPorProjeto_Success() throws Exception {
        Long projetoId = 10L;
        List<VagaProjetoResponseDTO> listaVagas = Collections.singletonList(vagaProjetoResponseDTO);
        when(vagaProjetoService.listarVagasPorProjeto(projetoId)).thenReturn(listaVagas);

        mockMvc.perform(get("/api/projetos/{projetoId}/vagas", projetoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(vagaProjetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve retornar vaga por ID")
    void getVaga_Success() throws Exception {
        Long projetoId = 10L;
        Long vagaId = 1L;
        when(vagaProjetoService.buscarVagaPorId(vagaId)).thenReturn(vagaProjetoResponseDTO);

        mockMvc.perform(get("/api/projetos/{projetoId}/vagas/{vagaId}", projetoId, vagaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vagaProjetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve editar uma vaga existente")
    void editarVaga_Success() throws Exception {
        Long projetoId = 10L;
        Long vagaId = 1L;
        when(vagaProjetoService.editarVagaProjeto(eq(vagaId), any(VagaProjetoCreateDTO.class)))
                .thenReturn(vagaProjetoResponseDTO);

        mockMvc.perform(put("/api/projetos/{projetoId}/vagas/{vagaId}", projetoId, vagaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vagaProjetoCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vagaProjetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve excluir uma vaga")
    void excluirVaga_Success() throws Exception {
        Long projetoId = 10L;
        Long vagaId = 1L;
        Long empresaId = 1L; // ID da empresa para a requisição
        doNothing().when(vagaProjetoService).excluirVagaProjeto(vagaId, empresaId);

        mockMvc.perform(delete("/api/projetos/{projetoId}/vagas/{vagaId}", projetoId, vagaId)
                        .param("empresaId", empresaId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}