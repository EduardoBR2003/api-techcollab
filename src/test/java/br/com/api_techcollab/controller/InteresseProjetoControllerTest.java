// src/test/java/br/com/api_techcollab/controller/InteresseProjetoControllerTest.java
package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.InteresseCreateDTO;
import br.com.api_techcollab.dto.InteresseProjetoResponseDTO;
import br.com.api_techcollab.dto.InteresseStatusUpdateDTO;
import br.com.api_techcollab.services.InteresseProjetoService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class InteresseProjetoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InteresseProjetoService interesseProjetoService;

    @InjectMocks
    private InteresseProjetoController interesseProjetoController;

    private ObjectMapper objectMapper;

    private InteresseProjetoResponseDTO interesseResponseDTO;
    private InteresseCreateDTO interesseCreateDTO;
    private InteresseStatusUpdateDTO statusUpdateDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(interesseProjetoController).build();

        interesseResponseDTO = new InteresseProjetoResponseDTO();
        interesseResponseDTO.setId(1L);

        interesseCreateDTO = new InteresseCreateDTO();
        interesseCreateDTO.setVagaProjetoId(10L);
        interesseCreateDTO.setMensagemMotivacao("Tenho interesse na vaga.");

        statusUpdateDTO = new InteresseStatusUpdateDTO();
        // Definir um status para o DTO de atualização, se relevante
    }

    @Test
    @DisplayName("Deve retornar interesses por vaga para empresa")
    void visualizarInteressadosPorVaga_Success() throws Exception {
        Long vagaId = 1L;
        Long empresaId = 1L;
        List<InteresseProjetoResponseDTO> listaInteresses = Collections.singletonList(interesseResponseDTO);

        when(interesseProjetoService.visualizarInteressadosPorVaga(vagaId, empresaId)).thenReturn(listaInteresses);

        mockMvc.perform(get("/api/empresas/{empresaId}/vagas/{vagaId}/interesses", empresaId, vagaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(interesseResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve atualizar status de interesse pela empresa")
    void atualizarStatusInteresseEmpresa_Success() throws Exception {
        Long interesseId = 1L;
        Long empresaId = 1L;

        when(interesseProjetoService.atualizarStatusInteresseEmpresa(eq(interesseId), any(InteresseStatusUpdateDTO.class), eq(empresaId)))
                .thenReturn(interesseResponseDTO);

        mockMvc.perform(put("/api/empresas/{empresaId}/interesses/{interesseId}", empresaId, interesseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(interesseResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve manifestar interesse por profissional")
    void manifestarInteresse_Success() throws Exception {
        Long profissionalId = 1L;

        when(interesseProjetoService.manifestarInteresse(eq(profissionalId), any(InteresseCreateDTO.class)))
                .thenReturn(interesseResponseDTO);

        mockMvc.perform(post("/api/profissionais/{profissionalId}/interesses", profissionalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(interesseCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(interesseResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve profissional responder alocação")
    void profissionalResponderAlocacao_Success() throws Exception {
        Long profissionalId = 1L;
        Long interesseId = 1L;

        when(interesseProjetoService.profissionalResponderAlocacao(eq(interesseId), any(InteresseStatusUpdateDTO.class), eq(profissionalId)))
                .thenReturn(interesseResponseDTO);

        mockMvc.perform(put("/api/profissionais/{profissionalId}/interesses/{interesseId}", profissionalId, interesseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(interesseResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve consultar status de interesses por profissional")
    void consultarStatusInteressesProfissional_Success() throws Exception {
        Long profissionalId = 1L;
        List<InteresseProjetoResponseDTO> listaInteresses = Collections.singletonList(interesseResponseDTO);

        when(interesseProjetoService.consultarStatusInteressesProfissional(profissionalId)).thenReturn(listaInteresses);

        mockMvc.perform(get("/api/profissionais/{profissionalId}/interesses", profissionalId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(interesseResponseDTO.getId()));
    }
}