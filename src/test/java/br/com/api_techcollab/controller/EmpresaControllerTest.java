// src/test/java/br/com/api_techcollab/controller/EmpresaControllerTest.java
package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.EmpresaCreateDTO;
import br.com.api_techcollab.dto.EmpresaResponseDTO;
import br.com.api_techcollab.dto.ProjetoCreateDTO;
import br.com.api_techcollab.dto.ProjetoResponseDTO;
import br.com.api_techcollab.services.EmpresaService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EmpresaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmpresaService empresaService;

    @Mock
    private ProjetoService projetoService;

    @InjectMocks
    private EmpresaController empresaController;

    private ObjectMapper objectMapper;

    private EmpresaResponseDTO empresaResponseDTO;
    private EmpresaCreateDTO empresaCreateDTO;
    private ProjetoResponseDTO projetoResponseDTO;
    private ProjetoCreateDTO projetoCreateDTO;

    @BeforeEach
    void setUp() {
        // Configura o MockMvc para testar o Controller isoladamente.
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(empresaController).build();

        empresaResponseDTO = new EmpresaResponseDTO();
        empresaResponseDTO.setId(1L);
        empresaResponseDTO.setNome("Empresa Teste");

        empresaCreateDTO = new EmpresaCreateDTO();
        empresaCreateDTO.setNome("Nova Empresa");
        empresaCreateDTO.setEmail("nova@empresa.com");
        empresaCreateDTO.setCnpj("11.111.111/0001-11");
        empresaCreateDTO.setSenha("senha123");

        projetoResponseDTO = new ProjetoResponseDTO();
        projetoResponseDTO.setId(10L);
        projetoResponseDTO.setTitulo("Projeto Teste");

        projetoCreateDTO = new ProjetoCreateDTO();
        projetoCreateDTO.setTitulo("Novo Projeto");
    }

    @Test
    @DisplayName("Deve retornar todas as empresas")
    void findAll_Success() throws Exception {
        // Configura o mock do serviço para retornar uma lista de empresas.
        List<EmpresaResponseDTO> listaEmpresas = Collections.singletonList(empresaResponseDTO);
        when(empresaService.findAll()).thenReturn(listaEmpresas);

        mockMvc.perform(get("/api/empresas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(empresaResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve retornar empresa por ID")
    void findById_Success() throws Exception {
        Long empresaId = 1L;
        when(empresaService.findById(empresaId)).thenReturn(empresaResponseDTO);

        mockMvc.perform(get("/api/empresas/{id}", empresaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(empresaResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve criar uma nova empresa")
    void create_Success() throws Exception {
        when(empresaService.create(any(EmpresaCreateDTO.class))).thenReturn(empresaResponseDTO);

        mockMvc.perform(post("/api/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empresaCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(empresaResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve atualizar uma empresa existente")
    void update_Success() throws Exception {
        Long empresaId = 1L;
        when(empresaService.update(eq(empresaId), any(EmpresaCreateDTO.class))).thenReturn(empresaResponseDTO);

        mockMvc.perform(put("/api/empresas/{id}", empresaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empresaCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(empresaResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve deletar uma empresa")
    void delete_Success() throws Exception {
        Long empresaId = 1L;
        doNothing().when(empresaService).delete(empresaId);

        mockMvc.perform(delete("/api/empresas/{id}", empresaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve criar um projeto para uma empresa")
    void criarProjetoDaEmpresa_Success() throws Exception {
        Long empresaId = 1L;
        when(projetoService.criarProjeto(any(ProjetoCreateDTO.class), eq(empresaId))).thenReturn(projetoResponseDTO);

        mockMvc.perform(post("/api/empresas/{empresaId}/projetos", empresaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projetoCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(projetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve consultar projetos por empresa")
    void consultarProjetosPorEmpresa_Success() throws Exception {
        Long empresaId = 1L;
        List<ProjetoResponseDTO> listaProjetos = Collections.singletonList(projetoResponseDTO);
        when(projetoService.consultarProjetosPorEmpresa(empresaId)).thenReturn(listaProjetos);

        mockMvc.perform(get("/api/empresas/{empresaId}/projetos", empresaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(projetoResponseDTO.getId()));
    }
}