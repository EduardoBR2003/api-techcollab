// src/test/java/br/com/api_techcollab/repository/EntregaProjetoRepositoryTest.java
package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.EntregaProjeto;
import br.com.api_techcollab.model.Projeto;
import br.com.api_techcollab.model.Empresa;
import br.com.api_techcollab.model.enums.StatusEntrega;
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
public class EntregaProjetoRepositoryTest {

    @Mock
    private EntregaProjetoRepository entregaProjetoRepository;

    private Projeto projeto;
    private EntregaProjeto entrega1;
    private EntregaProjeto entrega2;

    @BeforeEach
    void setUp() {
        Empresa empresa = new Empresa();
        empresa.setId(1L);

        projeto = new Projeto();
        projeto.setId(10L);
        projeto.setTitulo("Projeto Entregas");
        projeto.setEmpresa(empresa);

        entrega1 = new EntregaProjeto();
        entrega1.setId(1L);
        entrega1.setProjeto(projeto);
        entrega1.setDescEntrega("Documento de Requisitos");
        entrega1.setStatusEntrega(StatusEntrega.SUBMETIDA);
        entrega1.setDataSubmissao(new Date());

        entrega2 = new EntregaProjeto();
        entrega2.setId(2L);
        entrega2.setProjeto(projeto);
        entrega2.setDescEntrega("Código Fonte v1.0");
        entrega2.setStatusEntrega(StatusEntrega.APROVADA);
        entrega2.setDataSubmissao(new Date());
    }

    @Test
    @DisplayName("Deve encontrar entregas por ID de projeto")
    void findByProjetoId_ShouldReturnEntregas() {
        // Arrange
        List<EntregaProjeto> entregasDoProjeto = Arrays.asList(entrega1, entrega2);
        when(entregaProjetoRepository.findByProjetoId(projeto.getId())).thenReturn(entregasDoProjeto);

        // Act
        List<EntregaProjeto> foundEntregas = entregaProjetoRepository.findByProjetoId(projeto.getId());

        // Assert
        assertNotNull(foundEntregas);
        assertFalse(foundEntregas.isEmpty());
        assertEquals(2, foundEntregas.size());
        assertTrue(foundEntregas.contains(entrega1));
        assertTrue(foundEntregas.contains(entrega2));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para projeto sem entregas")
    void findByProjetoId_ShouldReturnEmptyList_WhenNoEntregas() {
        // Arrange
        Long nonExistentProjetoId = 99L;
        when(entregaProjetoRepository.findByProjetoId(nonExistentProjetoId)).thenReturn(Arrays.asList());

        // Act
        List<EntregaProjeto> foundEntregas = entregaProjetoRepository.findByProjetoId(nonExistentProjetoId);

        // Assert
        assertNotNull(foundEntregas);
        assertTrue(foundEntregas.isEmpty());
    }
}