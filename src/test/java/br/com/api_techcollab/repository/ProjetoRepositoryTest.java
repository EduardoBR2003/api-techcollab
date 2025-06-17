package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.Empresa;
import br.com.api_techcollab.model.Projeto;
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
public class ProjetoRepositoryTest {

    @Mock // Cria um mock do ProjetoRepository.
    private ProjetoRepository projetoRepository;

    private Empresa empresa;
    private Projeto projeto1;
    private Projeto projeto2;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        // Inicializa as entidades de teste.
        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNome("Empresa Teste SA");

        projeto1 = new Projeto();
        projeto1.setId(10L);
        projeto1.setTitulo("Projeto X");
        projeto1.setEmpresa(empresa);

        projeto2 = new Projeto();
        projeto2.setId(11L);
        projeto2.setTitulo("Projeto Y");
        projeto2.setEmpresa(empresa);
    }

    @Test // Marca o método como um teste.
    @DisplayName("Deve encontrar projetos por ID de empresa") // Nome amigável para o teste.
    void findByEmpresaId_ShouldReturnProjetos() {
        // Configura o mock do repositório para retornar uma lista de projetos quando findByEmpresaId for chamado.
        List<Projeto> projetosDaEmpresa = Arrays.asList(projeto1, projeto2);
        when(projetoRepository.findByEmpresaId(empresa.getId())).thenReturn(projetosDaEmpresa);

        // Executa o método do repositório.
        List<Projeto> foundProjetos = projetoRepository.findByEmpresaId(empresa.getId());

        // Verifica se a lista não é nula, não está vazia, tem o tamanho esperado e contém os projetos corretos.
        assertNotNull(foundProjetos);
        assertFalse(foundProjetos.isEmpty());
        assertEquals(2, foundProjetos.size());
        assertTrue(foundProjetos.contains(projeto1));
        assertTrue(foundProjetos.contains(projeto2));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para empresa sem projetos")
    void findByEmpresaId_ShouldReturnEmptyList_WhenNoProjetos() {
        // Define um ID de empresa que não existe.
        Long nonExistentEmpresaId = 99L;
        // Configura o mock do repositório para retornar uma lista vazia quando findByEmpresaId for chamado com um ID inexistente.
        when(projetoRepository.findByEmpresaId(nonExistentEmpresaId)).thenReturn(Arrays.asList());

        // Executa o método do repositório.
        List<Projeto> foundProjetos = projetoRepository.findByEmpresaId(nonExistentEmpresaId);

        // Verifica se a lista não é nula e está vazia.
        assertNotNull(foundProjetos);
        assertTrue(foundProjetos.isEmpty());
    }
}