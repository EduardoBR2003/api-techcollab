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

    @Mock // Cria um mock do EmpresaService para simular seu comportamento.
    private EmpresaService empresaService;

    @Mock // Cria um mock do ProjetoService para simular seu comportamento.
    private ProjetoService projetoService;

    @InjectMocks // Injeta os mocks no EmpresaController.
    private EmpresaController empresaController;

    private ObjectMapper objectMapper;

    private EmpresaResponseDTO empresaResponseDTO;
    private EmpresaCreateDTO empresaCreateDTO;
    private ProjetoResponseDTO projetoResponseDTO;
    private ProjetoCreateDTO projetoCreateDTO;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        // Configura o MockMvc para testar o Controller isoladamente, sem carregar o contexto completo do Spring.
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(empresaController).build();

        // Inicializa os DTOs de resposta e criação para serem usados nos testes.
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

    @Test // Marca o método como um teste.
    @DisplayName("Deve retornar todas as empresas") // Nome amigável para o teste.
    void findAll_Success() throws Exception {
        // Configura o mock do serviço para retornar uma lista contendo uma empresa de teste.
        List<EmpresaResponseDTO> listaEmpresas = Collections.singletonList(empresaResponseDTO);
        when(empresaService.findAll()).thenReturn(listaEmpresas);

        // Realiza uma requisição GET para "/api/empresas".
        mockMvc.perform(get("/api/empresas")
                        .contentType(MediaType.APPLICATION_JSON))
                // Espera um status HTTP 200 OK e verifica o ID da primeira empresa no JSON de resposta.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(empresaResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve retornar empresa por ID")
    void findById_Success() throws Exception {
        Long empresaId = 1L;
        // Configura o mock para retornar uma empresa específica quando findById for chamado com o ID 1L.
        when(empresaService.findById(empresaId)).thenReturn(empresaResponseDTO);

        // Realiza uma requisição GET para "/api/empresas/{id}".
        mockMvc.perform(get("/api/empresas/{id}", empresaId)
                        .contentType(MediaType.APPLICATION_JSON))
                // Espera um status HTTP 200 OK e verifica o ID da empresa no JSON de resposta.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(empresaResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve criar uma nova empresa")
    void create_Success() throws Exception {
        // Configura o mock para retornar a empresa de teste quando o método create for chamado com qualquer EmpresaCreateDTO.
        when(empresaService.create(any(EmpresaCreateDTO.class))).thenReturn(empresaResponseDTO);

        // Realiza uma requisição POST para "/api/empresas" com o DTO da nova empresa no corpo.
        mockMvc.perform(post("/api/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empresaCreateDTO)))
                // Espera um status HTTP 201 CREATED e verifica o ID da empresa criada no JSON de resposta.
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(empresaResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve atualizar uma empresa existente")
    void update_Success() throws Exception {
        Long empresaId = 1L;
        // Configura o mock para retornar a empresa de teste quando o método update for chamado com o ID 1L e qualquer EmpresaCreateDTO.
        when(empresaService.update(eq(empresaId), any(EmpresaCreateDTO.class))).thenReturn(empresaResponseDTO);

        // Realiza uma requisição PUT para "/api/empresas/{id}" com o DTO de atualização no corpo.
        mockMvc.perform(put("/api/empresas/{id}", empresaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empresaCreateDTO)))
                // Espera um status HTTP 200 OK e verifica o ID da empresa atualizada no JSON de resposta.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(empresaResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve deletar uma empresa")
    void delete_Success() throws Exception {
        Long empresaId = 1L;
        // Configura o mock para não fazer nada quando o método delete for chamado com o ID 1L.
        doNothing().when(empresaService).delete(empresaId);

        // Realiza uma requisição DELETE para "/api/empresas/{id}".
        mockMvc.perform(delete("/api/empresas/{id}", empresaId)
                        .contentType(MediaType.APPLICATION_JSON))
                // Espera um status HTTP 204 No Content.
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve criar um projeto para uma empresa")
    void criarProjetoDaEmpresa_Success() throws Exception {
        Long empresaId = 1L;
        // Configura o mock do projetoService para retornar um projeto de teste quando criarProjeto for chamado com qualquer ProjetoCreateDTO e o ID da empresa.
        when(projetoService.criarProjeto(any(ProjetoCreateDTO.class), eq(empresaId))).thenReturn(projetoResponseDTO);

        // Realiza uma requisição POST para "/api/empresas/{empresaId}/projetos".
        mockMvc.perform(post("/api/empresas/{empresaId}/projetos", empresaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projetoCreateDTO)))
                // Espera um status HTTP 201 CREATED e verifica o ID do projeto criado.
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(projetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve consultar projetos por empresa")
    void consultarProjetosPorEmpresa_Success() throws Exception {
        Long empresaId = 1L;
        // Configura o mock para retornar uma lista de projetos quando consultarProjetosPorEmpresa for chamado.
        List<ProjetoResponseDTO> listaProjetos = Collections.singletonList(projetoResponseDTO);
        when(projetoService.consultarProjetosPorEmpresa(empresaId)).thenReturn(listaProjetos);

        // Realiza uma requisição GET para "/api/empresas/{empresaId}/projetos".
        mockMvc.perform(get("/api/empresas/{empresaId}/projetos", empresaId)
                        .contentType(MediaType.APPLICATION_JSON))
                // Espera um status HTTP 200 OK e verifica o ID do primeiro projeto na lista.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(projetoResponseDTO.getId()));
    }
}