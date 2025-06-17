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

    @Mock // Cria um mock do AvaliacaoRepository.
    private AvaliacaoRepository avaliacaoRepository;

    private Projeto projeto;
    private Empresa avaliadorEmpresa;
    private Profissional avaliadoProfissional;
    private Avaliacao avaliacao1;
    private Avaliacao avaliacao2;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        // Inicializa as entidades de teste para simular cenários do repositório.
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

    @Test // Marca o método como um teste.
    @DisplayName("Deve encontrar avaliações por ID de projeto") // Nome amigável para o teste.
    void findByProjetoId_ShouldReturnAvaliacoes() {
        // Configura o mock do repositório para retornar uma lista de avaliações quando findByProjetoId for chamado com o ID do projeto.
        List<Avaliacao> avaliacoesDoProjeto = Arrays.asList(avaliacao1, avaliacao2);
        when(avaliacaoRepository.findByProjetoId(projeto.getId())).thenReturn(avaliacoesDoProjeto);

        // Executa o método do repositório que está sendo testado.
        List<Avaliacao> foundAvaliacoes = avaliacaoRepository.findByProjetoId(projeto.getId());

        // Verifica se a lista não é nula, não está vazia, tem o tamanho esperado e contém as avaliações corretas.
        assertNotNull(foundAvaliacoes);
        assertFalse(foundAvaliacoes.isEmpty());
        assertEquals(2, foundAvaliacoes.size());
        assertTrue(foundAvaliacoes.contains(avaliacao1));
        assertTrue(foundAvaliacoes.contains(avaliacao2));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para projeto sem avaliações")
    void findByProjetoId_ShouldReturnEmptyList_WhenNoAvaliacoes() {
        // Configura o mock do repositório para retornar uma lista vazia quando findByProjetoId for chamado com um ID de projeto inexistente.
        Long nonExistentProjetoId = 99L;
        when(avaliacaoRepository.findByProjetoId(nonExistentProjetoId)).thenReturn(Arrays.asList());

        // Executa o método do repositório.
        List<Avaliacao> foundAvaliacoes = avaliacaoRepository.findByProjetoId(nonExistentProjetoId);

        // Verifica se a lista não é nula e está vazia.
        assertNotNull(foundAvaliacoes);
        assertTrue(foundAvaliacoes.isEmpty());
    }
}