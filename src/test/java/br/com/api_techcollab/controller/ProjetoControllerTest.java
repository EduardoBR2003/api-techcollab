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

    @Mock // Cria um mock do ProjetoService.
    private ProjetoService projetoService;

    @InjectMocks // Injeta o mock no ProjetoController.
    private ProjetoController projetoController;

    private ObjectMapper objectMapper;

    private ProjetoResponseDTO projetoResponseDTO;
    private ProjetoUpdateDTO projetoUpdateDTO;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        objectMapper = new ObjectMapper();
        // Configura o MockMvc para testar o Controller isoladamente.
        mockMvc = MockMvcBuilders.standaloneSetup(projetoController).build();

        // Inicializa os DTOs de resposta e atualização para serem usados nos testes.
        projetoResponseDTO = new ProjetoResponseDTO();
        projetoResponseDTO.setId(1L);
        projetoResponseDTO.setTitulo("Projeto Teste");

        projetoUpdateDTO = new ProjetoUpdateDTO();
        projetoUpdateDTO.setTitulo("Projeto Atualizado");
        projetoUpdateDTO.setEmpresaId(10L); // Simula o ID da empresa para validação
    }

    @Test // Marca o método como um teste.
    @DisplayName("Deve retornar projetos disponíveis") // Nome amigável para o teste.
    void consultarProjetosDisponiveis_Success() throws Exception {
        // Configura o mock do serviço para retornar uma lista de projetos disponíveis.
        List<ProjetoResponseDTO> listaProjetos = Collections.singletonList(projetoResponseDTO);
        when(projetoService.consultarProjetosDisponiveis()).thenReturn(listaProjetos);

        // Realiza uma requisição GET para "/api/projetos/disponiveis".
        mockMvc.perform(get("/api/projetos/disponiveis")
                        .contentType(MediaType.APPLICATION_JSON))
                // Espera um status HTTP 200 OK e verifica o ID do primeiro projeto na lista.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(projetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve retornar projeto por ID")
    void buscarProjetoPorId_Success() throws Exception {
        Long projetoId = 1L;
        // Configura o mock do serviço para retornar um projeto específico quando buscarProjetoPorId for chamado.
        when(projetoService.buscarProjetoPorId(projetoId)).thenReturn(projetoResponseDTO);

        // Realiza uma requisição GET para "/api/projetos/{projetoId}".
        mockMvc.perform(get("/api/projetos/{projetoId}", projetoId)
                        .contentType(MediaType.APPLICATION_JSON))
                // Espera um status HTTP 200 OK e verifica o ID do projeto.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve editar um projeto")
    void editarProjeto_Success() throws Exception {
        Long projetoId = 1L;
        // Configura o mock do serviço para retornar o DTO de resposta quando editarProjeto for chamado.
        when(projetoService.editarProjeto(eq(projetoId), any(ProjetoUpdateDTO.class), eq(projetoUpdateDTO.getEmpresaId())))
                .thenReturn(projetoResponseDTO);

        // Realiza uma requisição PUT para "/api/projetos/{projetoId}" com o DTO de atualização no corpo.
        mockMvc.perform(put("/api/projetos/{projetoId}", projetoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projetoUpdateDTO)))
                // Espera um status HTTP 200 OK e verifica o ID do projeto atualizado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projetoResponseDTO.getId()));
    }

    @Test
    @DisplayName("Deve excluir um projeto")
    void excluirProjeto_Success() throws Exception {
        Long projetoId = 1L;
        Long empresaId = 10L; // ID da empresa para a requisição
        // Configura o mock do serviço para não fazer nada quando excluirProjeto for chamado.
        doNothing().when(projetoService).excluirProjeto(projetoId, empresaId);

        // Realiza uma requisição DELETE para "/api/projetos/{projetoId}" com o parâmetro empresaId.
        mockMvc.perform(delete("/api/projetos/{projetoId}", projetoId)
                        .param("empresaId", empresaId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                // Espera um status HTTP 204 No Content.
                .andExpect(status().isNoContent());
    }
}