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

    @Mock // Cria um mock do EquipeProjetoService.
    private EquipeProjetoService equipeProjetoService;

    @InjectMocks // Injeta o mock no EquipeProjetoController.
    private EquipeProjetoController equipeProjetoController;

    private ObjectMapper objectMapper;

    private EquipeProjetoResponseDTO equipeProjetoResponseDTO;
    private EquipeMontarDTO equipeMontarDTO;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        objectMapper = new ObjectMapper();
        // Configura o MockMvc para testar o Controller isoladamente.
        mockMvc = MockMvcBuilders.standaloneSetup(equipeProjetoController).build();

        // Inicializa os DTOs de resposta e montagem para serem usados nos testes.
        equipeProjetoResponseDTO = new EquipeProjetoResponseDTO();
        equipeProjetoResponseDTO.setId(1L);
        equipeProjetoResponseDTO.setNomeEquipe("Equipe Teste");
        equipeProjetoResponseDTO.setProjetoId(10L);

        equipeMontarDTO = new EquipeMontarDTO();
        equipeMontarDTO.setEmpresaId(1L);
        equipeMontarDTO.setIdsInteressesSelecionados(Collections.singletonList(100L));
        equipeMontarDTO.setNomeEquipeSugerido("Nova Equipe");
    }

    @Test // Marca o método como um teste.
    @DisplayName("Deve montar a equipe para um projeto com sucesso") // Nome amigável para o teste.
    void montarEquipe_Success() throws Exception {
        Long projetoId = 10L;

        // Configura o mock do serviço para retornar uma equipe de projeto de teste quando montarEquipe for chamado com os parâmetros esperados.
        when(equipeProjetoService.montarEquipe(
                eq(projetoId),
                eq(equipeMontarDTO.getIdsInteressesSelecionados()),
                eq(equipeMontarDTO.getNomeEquipeSugerido()),
                eq(equipeMontarDTO.getEmpresaId())
        )).thenReturn(equipeProjetoResponseDTO);

        // Realiza uma requisição POST para o endpoint de montagem de equipe.
        mockMvc.perform(post("/api/projetos/{projetoId}/equipe", projetoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipeMontarDTO)))
                // Espera um status HTTP 200 OK e verifica os atributos da equipe montada no JSON de resposta.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(equipeProjetoResponseDTO.getId()))
                .andExpect(jsonPath("$.nomeEquipe").value(equipeProjetoResponseDTO.getNomeEquipe()));
    }

    @Test
    @DisplayName("Deve visualizar a equipe de um projeto")
    void visualizarEquipeProjeto_Success() throws Exception {
        Long projetoId = 10L;
        // Configura o mock do serviço para retornar uma equipe de projeto de teste quando visualizarEquipeProjeto for chamado.
        when(equipeProjetoService.visualizarEquipeProjeto(projetoId)).thenReturn(equipeProjetoResponseDTO);

        // Realiza uma requisição GET para o endpoint de visualização de equipe.
        mockMvc.perform(get("/api/projetos/{projetoId}/equipe", projetoId)
                        .contentType(MediaType.APPLICATION_JSON))
                // Espera um status HTTP 200 OK e verifica os atributos da equipe no JSON de resposta.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(equipeProjetoResponseDTO.getId()))
                .andExpect(jsonPath("$.projetoId").value(equipeProjetoResponseDTO.getProjetoId()));
    }
}