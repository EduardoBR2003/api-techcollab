// src/test/java/br/com/api_techcollab/controller/ProjetoControllerTest.java
package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.ProjetoUpdateDTO;
import br.com.api_techcollab.dto.ProjetoResponseDTO;
import br.com.api_techcollab.services.ProjetoService;
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
public class ProjetoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjetoService projetoService;

    @InjectMocks
    private ProjetoController projetoController;

    private ObjectMapper objectMapper;

    private ProjetoResponseDTO projetoResponseDTO;
    private ProjetoUpdateDTO projetoUpdateDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(projetoController).build();

        projetoResponseDTO = new ProjetoResponseDTO();
        projetoResponseDTO.setId(1L);
        projetoResponseDTO.setTitulo("Projeto Teste");

        projetoUpdateDTO = new ProjetoUpdateDTO();
        projetoUpdateDTO.setTitulo("Projeto Atualizado");
        projetoUpdateDTO.setEmpresaId(10L); // Simula o ID da empresa para validação
    }

    @Test
    @DisplayName("Deve retornar projetos disponíveis")
    void consultarProjetosDisponiveis_Success() throws Exception {
        List<ProjetoResponseDTO> listaProjetos = Collections.singletonList(projetoResponseDTO);
        when(projetoService.consultarProjetosDisponiveis()).thenReturn(listaProjetos);

        mockMvc.perform(get("/api/projetos/disponiveis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(projetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve retornar projeto por ID")
    void buscarProjetoPorId_Success() throws Exception {
        Long projetoId = 1L;
        when(projetoService.buscarProjetoPorId(projetoId)).thenReturn(projetoResponseDTO);

        mockMvc.perform(get("/api/projetos/{projetoId}", projetoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve editar um projeto")
    void editarProjeto_Success() throws Exception {
        Long projetoId = 1L;
        when(projetoService.editarProjeto(eq(projetoId), any(ProjetoUpdateDTO.class), eq(projetoUpdateDTO.getEmpresaId())))
                .thenReturn(projetoResponseDTO);

        mockMvc.perform(put("/api/projetos/{projetoId}", projetoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projetoUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve excluir um projeto")
    void excluirProjeto_Success() throws Exception {
        Long projetoId = 1L;
        Long empresaId = 10L; // ID da empresa para a requisição
        doNothing().when(projetoService).excluirProjeto(projetoId, empresaId);

        mockMvc.perform(delete("/api/projetos/{projetoId}", projetoId)
                        .param("empresaId", empresaId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}