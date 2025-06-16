// src/test/java/br/com/api_techcollab/repository/MembroEquipeRepositoryTest.java
package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.EquipeProjeto;
import br.com.api_techcollab.model.MembroEquipe;
import br.com.api_techcollab.model.Profissional;
import br.com.api_techcollab.model.Projeto;
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
public class MembroEquipeRepositoryTest {

    @Mock
    private MembroEquipeRepository membroEquipeRepository;

    private EquipeProjeto equipeProjeto;
    private Profissional profissional1;
    private Profissional profissional2;
    private MembroEquipe membro1;
    private MembroEquipe membro2;

    @BeforeEach
    void setUp() {
        Projeto projeto = new Projeto();
        projeto.setId(1L);

        equipeProjeto = new EquipeProjeto();
        equipeProjeto.setId(10L);
        equipeProjeto.setProjeto(projeto);
        equipeProjeto.setNomeEquipe("Equipe Delta");

        profissional1 = new Profissional();
        profissional1.setId(100L);
        profissional1.setNome("Pro1");

        profissional2 = new Profissional();
        profissional2.setId(101L);
        profissional2.setNome("Pro2");

        membro1 = new MembroEquipe();
        membro1.setId(1L);
        membro1.setEquipeProjeto(equipeProjeto);
        membro1.setProfissional(profissional1);
        membro1.setPapel("Desenvolvedor");
        membro1.setDataEntrada(new Date());

        membro2 = new MembroEquipe();
        membro2.setId(2L);
        membro2.setEquipeProjeto(equipeProjeto);
        membro2.setProfissional(profissional2);
        membro2.setPapel("QA");
        membro2.setDataEntrada(new Date());
    }

    @Test
    @DisplayName("Deve encontrar todos os membros de uma equipe específica")
    void findByEquipeProjetoId_ShouldReturnMembers() {
        // Arrange
        List<MembroEquipe> membrosDaEquipe = Arrays.asList(membro1, membro2);
        when(membroEquipeRepository.findByEquipeProjetoId(equipeProjeto.getId())).thenReturn(membrosDaEquipe);

        // Act
        List<MembroEquipe> foundMembers = membroEquipeRepository.findByEquipeProjetoId(equipeProjeto.getId());

        // Assert
        assertNotNull(foundMembers);
        assertFalse(foundMembers.isEmpty());
        assertEquals(2, foundMembers.size());
        assertTrue(foundMembers.contains(membro1));
        assertTrue(foundMembers.contains(membro2));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para equipe sem membros")
    void findByEquipeProjetoId_ShouldReturnEmptyList_WhenNoMembers() {
        // Arrange
        Long nonExistentEquipeId = 99L;
        when(membroEquipeRepository.findByEquipeProjetoId(nonExistentEquipeId)).thenReturn(Arrays.asList());

        // Act
        List<MembroEquipe> foundMembers = membroEquipeRepository.findByEquipeProjetoId(nonExistentEquipeId);

        // Assert
        assertNotNull(foundMembers);
        assertTrue(foundMembers.isEmpty());
    }
}