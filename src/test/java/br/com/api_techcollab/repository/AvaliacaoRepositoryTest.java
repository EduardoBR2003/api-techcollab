// src/test/java/br/com/api_techcollab/repository/AvaliacaoRepositoryTest.java
package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.Avaliacao;
import br.com.api_techcollab.model.Empresa;
import br.com.api_techcollab.model.Profissional;
import br.com.api_techcollab.model.Projeto;
import br.com.api_techcollab.model.enums.TipoAvaliado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvaliacaoRepositoryTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    private Projeto projeto;
    private Empresa avaliadorEmpresa;
    private Profissional avaliadoProfissional;
    private Avaliacao avaliacao1;
    private Avaliacao avaliacao2;

    @BeforeEach
    void setUp() {
        avaliadorEmpresa = new Empresa();
        avaliadorEmpresa.setId(1L);

        avaliadoProfissional = new Profissional();
        avaliadoProfissional.setId(2L);

        projeto = new Projeto();
        projeto.setId(10L);
        projeto.setTitulo("Projeto Avaliação");
        projeto.setEmpresa(avaliadorEmpresa);

        avaliacao1 = new Avaliacao();
        avaliacao1.setId(1L);
        avaliacao1.setProjeto(projeto);
        avaliacao1.setAvaliador(avaliadorEmpresa);
        avaliacao1.setAvaliado(avaliadoProfissional);
        avaliacao1.setTipoAvaliado(TipoAvaliado.PROFISSIONAL);
        avaliacao1.setNota(5);
        avaliacao1.setDataAvaliacao(new Date());

        avaliacao2 = new Avaliacao();
        avaliacao2.setId(2L);
        avaliacao2.setProjeto(projeto);
        avaliacao2.setAvaliador(avaliadoProfissional); // Profissional avalia
        avaliacao2.setAvaliado(avaliadorEmpresa); // Empresa avaliada
        avaliacao2.setTipoAvaliado(TipoAvaliado.EMPRESA);
        avaliacao2.setNota(4);
        avaliacao2.setDataAvaliacao(new Date());
    }

    @Test
    @DisplayName("Deve encontrar avaliações por ID de projeto")
    void findByProjetoId_ShouldReturnAvaliacoes() {
        // Arrange
        List<Avaliacao> avaliacoesDoProjeto = Arrays.asList(avaliacao1, avaliacao2);
        when(avaliacaoRepository.findByProjetoId(projeto.getId())).thenReturn(avaliacoesDoProjeto);

        // Act
        List<Avaliacao> foundAvaliacoes = avaliacaoRepository.findByProjetoId(projeto.getId());

        // Assert
        assertNotNull(foundAvaliacoes);
        assertFalse(foundAvaliacoes.isEmpty());
        assertEquals(2, foundAvaliacoes.size());
        assertTrue(foundAvaliacoes.contains(avaliacao1));
        assertTrue(foundAvaliacoes.contains(avaliacao2));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para projeto sem avaliações")
    void findByProjetoId_ShouldReturnEmptyList_WhenNoAvaliacoes() {
        // Arrange
        Long nonExistentProjetoId = 99L;
        when(avaliacaoRepository.findByProjetoId(nonExistentProjetoId)).thenReturn(Arrays.asList());

        // Act
        List<Avaliacao> foundAvaliacoes = avaliacaoRepository.findByProjetoId(nonExistentProjetoId);

        // Assert
        assertNotNull(foundAvaliacoes);
        assertTrue(foundAvaliacoes.isEmpty());
    }
}