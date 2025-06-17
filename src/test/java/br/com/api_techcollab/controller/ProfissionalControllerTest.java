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

    @Mock // Cria um mock do ProfissionalService.
    private ProfissionalService profissionalService;

    @InjectMocks // Injeta o mock no ProfissionalController.
    private ProfissionalController profissionalController;

    private ObjectMapper objectMapper;

    private ProfissionalResponseDTO profissionalResponseDTO;
    private ProfissionalCreateDTO profissionalCreateDTO;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        objectMapper = new ObjectMapper();
        // Configura o MockMvc para testar o Controller isoladamente.
        mockMvc = MockMvcBuilders.standaloneSetup(profissionalController).build();

        // Inicializa os DTOs de resposta e criação para serem usados nos testes.
        profissionalResponseDTO = new ProfissionalResponseDTO();
        profissionalResponseDTO.setId(1L);
        profissionalResponseDTO.setNome("Profissional Teste");

        profissionalCreateDTO = new ProfissionalCreateDTO();
        profissionalCreateDTO.setNome("Novo Profissional");
        profissionalCreateDTO.setEmail("novo@profissional.com");
        profissionalCreateDTO.setSenha("senha123");
    }

    @Test // Marca o método como um teste.
    @DisplayName("Deve retornar todos os profissionais") // Nome amigável para o teste.
    void findAll_Success() throws Exception {
        // Configura o mock do serviço para retornar uma lista de profissionais de teste.
        List<ProfissionalResponseDTO> listaProfissionais = Collections.singletonList(profissionalResponseDTO);
        when(profissionalService.findAll()).thenReturn(listaProfissionais);

        // Realiza uma requisição GET para "/api/profissionais".
        mockMvc.perform(get("/api/profissionais")
                        .contentType(MediaType.APPLICATION_JSON))
                // Espera um status HTTP 200 OK e verifica o ID do primeiro profissional na lista.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(profissionalResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve retornar profissional por ID")
    void findById_Success() throws Exception {
        Long profissionalId = 1L;
        // Configura o mock do serviço para retornar um profissional específico quando findById for chamado.
        when(profissionalService.findById(profissionalId)).thenReturn(profissionalResponseDTO);

        // Realiza uma requisição GET para "/api/profissionais/{id}".
        mockMvc.perform(get("/api/profissionais/{id}", profissionalId)
                        .contentType(MediaType.APPLICATION_JSON))
                // Espera um status HTTP 200 OK e verifica o ID do profissional.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profissionalResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve criar um novo profissional")
    void create_Success() throws Exception {
        // Configura o mock do serviço para retornar o DTO de resposta quando create for chamado.
        when(profissionalService.create(any(ProfissionalCreateDTO.class))).thenReturn(profissionalResponseDTO);

        // Realiza uma requisição POST para "/api/profissionais" com o DTO de criação no corpo.
        mockMvc.perform(post("/api/profissionais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profissionalCreateDTO)))
                // Espera um status HTTP 201 CREATED e verifica o ID do profissional criado.
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(profissionalResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve atualizar um profissional existente")
    void update_Success() throws Exception {
        Long profissionalId = 1L;
        // Configura o mock do serviço para retornar o DTO de resposta quando update for chamado.
        when(profissionalService.update(eq(profissionalId), any(ProfissionalCreateDTO.class))).thenReturn(profissionalResponseDTO);

        // Realiza uma requisição PUT para "/api/profissionais/{id}" com o DTO de atualização no corpo.
        mockMvc.perform(put("/api/profissionais/{id}", profissionalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profissionalCreateDTO)))
                // Espera um status HTTP 200 OK e verifica o ID do profissional atualizado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profissionalResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve deletar um profissional")
    void delete_Success() throws Exception {
        Long profissionalId = 1L;
        // Configura o mock do serviço para não fazer nada quando delete for chamado.
        doNothing().when(profissionalService).delete(profissionalId);

        // Realiza uma requisição DELETE para "/api/profissionais/{id}".
        mockMvc.perform(delete("/api/profissionais/{id}", profissionalId)
                        .contentType(MediaType.APPLICATION_JSON))
                // Espera um status HTTP 204 No Content.
                .andExpect(status().isNoContent());
    }
}