package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.Empresa;
import br.com.api_techcollab.model.Projeto;
import br.com.api_techcollab.model.VagaProjeto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VagaProjetoRepositoryTest {

    @Mock // Cria um mock do VagaProjetoRepository.
    private VagaProjetoRepository vagaProjetoRepository;

    private Projeto projeto;
    private VagaProjeto vaga1;
    private VagaProjeto vaga2;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        // Inicializa as entidades de teste.
        Empresa empresa = new Empresa();
        empresa.setId(1L);

        projeto = new Projeto();
        projeto.setId(10L);
        projeto.setTitulo("Projeto Omega");
        projeto.setEmpresa(empresa);

        vaga1 = new VagaProjeto();
        vaga1.setId(100L);
        vaga1.setProjeto(projeto);
        vaga1.setTituloVaga("Engenheiro de Software");

        vaga2 = new VagaProjeto();
        vaga2.setId(101L);
        vaga2.setProjeto(projeto);
        vaga2.setTituloVaga("UX Designer");
    }

    @Test // Marca o método como um teste.
    @DisplayName("Deve encontrar vagas por ID de projeto") // Nome amigável para o teste.
    void findByProjetoId_ShouldReturnVagas() {
        // Configura o mock do repositório para retornar uma lista de vagas quando findByProjetoId for chamado com o ID do projeto.
        List<VagaProjeto> vagasDoProjeto = Arrays.asList(vaga1, vaga2);
        when(vagaProjetoRepository.findByProjetoId(projeto.getId())).thenReturn(vagasDoProjeto);

        // Executa o método do repositório.
        List<VagaProjeto> foundVagas = vagaProjetoRepository.findByProjetoId(projeto.getId());

        // Verifica se a lista não é nula, não está vazia, tem o tamanho esperado e contém as vagas corretas.
        assertNotNull(foundVagas);
        assertFalse(foundVagas.isEmpty());
        assertEquals(2, foundVagas.size());
        assertTrue(foundVagas.contains(vaga1));
        assertTrue(foundVagas.contains(vaga2));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para projeto sem vagas")
    void findByProjetoId_ShouldReturnEmptyList_WhenNoVagas() {
        // Define um ID de projeto que não existe.
        Long nonExistentProjetoId = 99L;
        // Configura o mock do repositório para retornar uma lista vazia quando findByProjetoId for chamado com um ID inexistente.
        when(vagaProjetoRepository.findByProjetoId(nonExistentProjetoId)).thenReturn(Arrays.asList());

        // Executa o método do repositório.
        List<VagaProjeto> foundVagas = vagaProjetoRepository.findByProjetoId(nonExistentProjetoId);

        // Verifica se a lista não é nula e está vazia.
        assertNotNull(foundVagas);
        assertTrue(foundVagas.isEmpty());
    }
}