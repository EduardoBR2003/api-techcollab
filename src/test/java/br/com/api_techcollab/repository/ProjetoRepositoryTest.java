// src/test/java/br/com/api_techcollab/repository/ProjetoRepositoryTest.java
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

    @Mock
    private ProjetoRepository projetoRepository;

    private Empresa empresa;
    private Projeto projeto1;
    private Projeto projeto2;

    @BeforeEach
    void setUp() {
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

    @Test
    @DisplayName("Deve encontrar projetos por ID de empresa")
    void findByEmpresaId_ShouldReturnProjetos() {
        // Arrange
        List<Projeto> projetosDaEmpresa = Arrays.asList(projeto1, projeto2);
        when(projetoRepository.findByEmpresaId(empresa.getId())).thenReturn(projetosDaEmpresa);

        // Act
        List<Projeto> foundProjetos = projetoRepository.findByEmpresaId(empresa.getId());

        // Assert
        assertNotNull(foundProjetos);
        assertFalse(foundProjetos.isEmpty());
        assertEquals(2, foundProjetos.size());
        assertTrue(foundProjetos.contains(projeto1));
        assertTrue(foundProjetos.contains(projeto2));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para empresa sem projetos")
    void findByEmpresaId_ShouldReturnEmptyList_WhenNoProjetos() {
        // Arrange
        Long nonExistentEmpresaId = 99L;
        when(projetoRepository.findByEmpresaId(nonExistentEmpresaId)).thenReturn(Arrays.asList());

        // Act
        List<Projeto> foundProjetos = projetoRepository.findByEmpresaId(nonExistentEmpresaId);

        // Assert
        assertNotNull(foundProjetos);
        assertTrue(foundProjetos.isEmpty());
    }
}