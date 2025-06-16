// src/test/java/br/com/api_techcollab/controller/EquipeProjetoControllerTest.java
package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.EquipeMontarDTO;
import br.com.api_techcollab.dto.EquipeProjetoResponseDTO;
import br.com.api_techcollab.services.EquipeProjetoService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EquipeProjetoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EquipeProjetoService equipeProjetoService;

    @InjectMocks
    private EquipeProjetoController equipeProjetoController;

    private ObjectMapper objectMapper;

    private EquipeProjetoResponseDTO equipeProjetoResponseDTO;
    private EquipeMontarDTO equipeMontarDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(equipeProjetoController).build();

        equipeProjetoResponseDTO = new EquipeProjetoResponseDTO();
        equipeProjetoResponseDTO.setId(1L);
        equipeProjetoResponseDTO.setNomeEquipe("Equipe Teste");
        equipeProjetoResponseDTO.setProjetoId(10L);

        equipeMontarDTO = new EquipeMontarDTO();
        equipeMontarDTO.setEmpresaId(1L);
        equipeMontarDTO.setIdsInteressesSelecionados(Collections.singletonList(100L));
        equipeMontarDTO.setNomeEquipeSugerido("Nova Equipe");
    }

    @Test
    @DisplayName("Deve montar a equipe para um projeto com sucesso")
    void montarEquipe_Success() throws Exception {
        Long projetoId = 10L;

        when(equipeProjetoService.montarEquipe(
                eq(projetoId),
                eq(equipeMontarDTO.getIdsInteressesSelecionados()),
                eq(equipeMontarDTO.getNomeEquipeSugerido()),
                eq(equipeMontarDTO.getEmpresaId())
        )).thenReturn(equipeProjetoResponseDTO);

        mockMvc.perform(post("/api/projetos/{projetoId}/equipe", projetoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipeMontarDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(equipeProjetoResponseDTO.getId()))
                .andExpect(jsonPath("$.nomeEquipe").value(equipeProjetoResponseDTO.getNomeEquipe()));
    }

    @Test
    @DisplayName("Deve visualizar a equipe de um projeto")
    void visualizarEquipeProjeto_Success() throws Exception {
        Long projetoId = 10L;
        when(equipeProjetoService.visualizarEquipeProjeto(projetoId)).thenReturn(equipeProjetoResponseDTO);

        mockMvc.perform(get("/api/projetos/{projetoId}/equipe", projetoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(equipeProjetoResponseDTO.getId()))
                .andExpect(jsonPath("$.projetoId").value(equipeProjetoResponseDTO.getProjetoId()));
    }
}