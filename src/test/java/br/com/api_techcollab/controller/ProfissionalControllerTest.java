// src/test/java/br/com/api_techcollab/controller/ProfissionalControllerTest.java
package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.ProfissionalCreateDTO;
import br.com.api_techcollab.dto.ProfissionalResponseDTO;
import br.com.api_techcollab.services.ProfissionalService;
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
public class ProfissionalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProfissionalService profissionalService;

    @InjectMocks
    private ProfissionalController profissionalController;

    private ObjectMapper objectMapper;

    private ProfissionalResponseDTO profissionalResponseDTO;
    private ProfissionalCreateDTO profissionalCreateDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(profissionalController).build();

        profissionalResponseDTO = new ProfissionalResponseDTO();
        profissionalResponseDTO.setId(1L);
        profissionalResponseDTO.setNome("Profissional Teste");

        profissionalCreateDTO = new ProfissionalCreateDTO();
        profissionalCreateDTO.setNome("Novo Profissional");
        profissionalCreateDTO.setEmail("novo@profissional.com");
        profissionalCreateDTO.setSenha("senha123");
    }

    @Test
    @DisplayName("Deve retornar todos os profissionais")
    void findAll_Success() throws Exception {
        List<ProfissionalResponseDTO> listaProfissionais = Collections.singletonList(profissionalResponseDTO);
        when(profissionalService.findAll()).thenReturn(listaProfissionais);

        mockMvc.perform(get("/api/profissionais")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(profissionalResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve retornar profissional por ID")
    void findById_Success() throws Exception {
        Long profissionalId = 1L;
        when(profissionalService.findById(profissionalId)).thenReturn(profissionalResponseDTO);

        mockMvc.perform(get("/api/profissionais/{id}", profissionalId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profissionalResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve criar um novo profissional")
    void create_Success() throws Exception {
        when(profissionalService.create(any(ProfissionalCreateDTO.class))).thenReturn(profissionalResponseDTO);

        mockMvc.perform(post("/api/profissionais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profissionalCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(profissionalResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve atualizar um profissional existente")
    void update_Success() throws Exception {
        Long profissionalId = 1L;
        when(profissionalService.update(eq(profissionalId), any(ProfissionalCreateDTO.class))).thenReturn(profissionalResponseDTO);

        mockMvc.perform(put("/api/profissionais/{id}", profissionalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profissionalCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profissionalResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve deletar um profissional")
    void delete_Success() throws Exception {
        Long profissionalId = 1L;
        doNothing().when(profissionalService).delete(profissionalId);

        mockMvc.perform(delete("/api/profissionais/{id}", profissionalId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}