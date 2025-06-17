package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.InteresseProjeto;
import br.com.api_techcollab.model.Profissional;
import br.com.api_techcollab.model.Projeto;
import br.com.api_techcollab.model.VagaProjeto;
import br.com.api_techcollab.model.Empresa;
import br.com.api_techcollab.model.enums.StatusInteresse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InteresseProjetoRepositoryTest {

    @Mock // Cria um mock do InteresseProjetoRepository.
    private InteresseProjetoRepository interesseProjetoRepository;

    private Profissional profissional;
    private VagaProjeto vagaProjeto1;
    private VagaProjeto vagaProjeto2;
    private InteresseProjeto interesse1;
    private InteresseProjeto interesse2;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        // Inicializa as entidades de teste.
        profissional = new Profissional();
        profissional.setId(1L);
        profissional.setNome("Profissional Teste");

        Empresa empresa = new Empresa();
        empresa.setId(10L);

        Projeto projeto1 = new Projeto();
        projeto1.setId(100L);
        projeto1.setEmpresa(empresa);

        Projeto projeto2 = new Projeto();
        projeto2.setId(101L);
        projeto2.setEmpresa(empresa);

        vagaProjeto1 = new VagaProjeto();
        vagaProjeto1.setId(1000L);
        vagaProjeto1.setProjeto(projeto1);
        vagaProjeto1.setTituloVaga("Dev Backend");

        vagaProjeto2 = new VagaProjeto();
        vagaProjeto2.setId(1001L);
        vagaProjeto2.setProjeto(projeto2);
        vagaProjeto2.setTituloVaga("Dev Frontend");

        interesse1 = new InteresseProjeto();
        interesse1.setId(1L);
        interesse1.setProfissional(profissional);
        interesse1.setVagaProjeto(vagaProjeto1);
        interesse1.setStatusInteresse(StatusInteresse.PENDENTE);

        interesse2 = new InteresseProjeto();
        interesse2.setId(2L);
        interesse2.setProfissional(profissional);
        interesse2.setVagaProjeto(vagaProjeto2);
        interesse2.setStatusInteresse(StatusInteresse.SELECIONADO);
    }

    @Test // Marca o método como um teste.
    @DisplayName("Deve encontrar interesses por ID de profissional") // Nome amigável para o teste.
    void findByProfissionalId_ShouldReturnInteresses() {
        // Configura o mock do repositório para retornar uma lista de interesses quando findByProfissionalId for chamado.
        List<InteresseProjeto> interessesDoProfissional = Arrays.asList(interesse1, interesse2);
        when(interesseProjetoRepository.findByProfissionalId(profissional.getId())).thenReturn(interessesDoProfissional);

        // Executa o método do repositório.
        List<InteresseProjeto> foundInteresses = interesseProjetoRepository.findByProfissionalId(profissional.getId());

        // Verifica se a lista não é nula, não está vazia, tem o tamanho esperado e contém os interesses corretos.
        assertNotNull(foundInteresses);
        assertFalse(foundInteresses.isEmpty());
        assertEquals(2, foundInteresses.size());
        assertTrue(foundInteresses.contains(interesse1));
        assertTrue(foundInteresses.contains(interesse2));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para profissional sem interesses")
    void findByProfissionalId_ShouldReturnEmptyList_WhenNoInterests() {
        // Define um ID de profissional que não existe.
        Long nonExistentProfissionalId = 99L;
        // Configura o mock do repositório para retornar uma lista vazia quando findByProfissionalId for chamado com um ID inexistente.
        when(interesseProjetoRepository.findByProfissionalId(nonExistentProfissionalId)).thenReturn(Arrays.asList());

        // Executa o método do repositório.
        List<InteresseProjeto> foundInteresses = interesseProjetoRepository.findByProfissionalId(nonExistentProfissionalId);

        // Verifica se a lista não é nula e está vazia.
        assertNotNull(foundInteresses);
        assertTrue(foundInteresses.isEmpty());
    }

    @Test
    @DisplayName("Deve encontrar interesses por ID de vaga de projeto")
    void findByVagaProjetoId_ShouldReturnInteresses() {
        // Configura o mock do repositório para retornar uma lista de interesses quando findByVagaProjetoId for chamado.
        List<InteresseProjeto> interessesDaVaga = Collections.singletonList(interesse1);
        when(interesseProjetoRepository.findByVagaProjetoId(vagaProjeto1.getId())).thenReturn(interessesDaVaga);

        // Executa o método do repositório.
        List<InteresseProjeto> foundInteresses = interesseProjetoRepository.findByVagaProjetoId(vagaProjeto1.getId());

        // Verifica se a lista não é nula, não está vazia, tem o tamanho esperado e contém o interesse correto.
        assertNotNull(foundInteresses);
        assertFalse(foundInteresses.isEmpty());
        assertEquals(1, foundInteresses.size());
        assertTrue(foundInteresses.contains(interesse1));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para vaga sem interesses")
    void findByVagaProjetoId_ShouldReturnEmptyList_WhenNoInterests() {
        // Define um ID de vaga que não existe.
        Long nonExistentVagaId = 9999L;
        // Configura o mock do repositório para retornar uma lista vazia quando findByVagaProjetoId for chamado com um ID inexistente.
        when(interesseProjetoRepository.findByVagaProjetoId(nonExistentVagaId)).thenReturn(Arrays.asList());

        // Executa o método do repositório.
        List<InteresseProjeto> foundInteresses = interesseProjetoRepository.findByVagaProjetoId(nonExistentVagaId);

        // Verifica se a lista não é nula e está vazia.
        assertNotNull(foundInteresses);
        assertTrue(foundInteresses.isEmpty());
    }
}