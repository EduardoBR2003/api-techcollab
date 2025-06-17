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

    @Mock // Cria um mock do EntregaProjetoRepository.
    private EntregaProjetoRepository entregaProjetoRepository;

    private Projeto projeto;
    private EntregaProjeto entrega1;
    private EntregaProjeto entrega2;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        // Inicializa as entidades de teste.
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

    @Test // Marca o método como um teste.
    @DisplayName("Deve encontrar entregas por ID de projeto") // Nome amigável para o teste.
    void findByProjetoId_ShouldReturnEntregas() {
        // Configura o mock do repositório para retornar uma lista de entregas quando findByProjetoId for chamado com o ID do projeto.
        List<EntregaProjeto> entregasDoProjeto = Arrays.asList(entrega1, entrega2);
        when(entregaProjetoRepository.findByProjetoId(projeto.getId())).thenReturn(entregasDoProjeto);

        // Executa o método do repositório.
        List<EntregaProjeto> foundEntregas = entregaProjetoRepository.findByProjetoId(projeto.getId());

        // Verifica se a lista não é nula, não está vazia, tem o tamanho esperado e contém as entregas corretas.
        assertNotNull(foundEntregas);
        assertFalse(foundEntregas.isEmpty());
        assertEquals(2, foundEntregas.size());
        assertTrue(foundEntregas.contains(entrega1));
        assertTrue(foundEntregas.contains(entrega2));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para projeto sem entregas")
    void findByProjetoId_ShouldReturnEmptyList_WhenNoEntregas() {
        // Configura o mock do repositório para retornar uma lista vazia quando findByProjetoId for chamado com um ID de projeto inexistente.
        Long nonExistentProjetoId = 99L;
        when(entregaProjetoRepository.findByProjetoId(nonExistentProjetoId)).thenReturn(Arrays.asList());

        // Executa o método do repositório.
        List<EntregaProjeto> foundEntregas = entregaProjetoRepository.findByProjetoId(nonExistentProjetoId);

        // Verifica se a lista não é nula e está vazia.
        assertNotNull(foundEntregas);
        assertTrue(foundEntregas.isEmpty());
    }
}