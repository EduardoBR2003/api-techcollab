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

    @Mock // Cria um mock do MembroEquipeRepository.
    private MembroEquipeRepository membroEquipeRepository;

    private EquipeProjeto equipeProjeto;
    private Profissional profissional1;
    private Profissional profissional2;
    private MembroEquipe membro1;
    private MembroEquipe membro2;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        // Inicializa as entidades de teste.
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

    @Test // Marca o método como um teste.
    @DisplayName("Deve encontrar todos os membros de uma equipe específica") // Nome amigável para o teste.
    void findByEquipeProjetoId_ShouldReturnMembers() {
        // Configura o mock do repositório para retornar uma lista de membros quando findByEquipeProjetoId for chamado.
        List<MembroEquipe> membrosDaEquipe = Arrays.asList(membro1, membro2);
        when(membroEquipeRepository.findByEquipeProjetoId(equipeProjeto.getId())).thenReturn(membrosDaEquipe);

        // Executa o método do repositório.
        List<MembroEquipe> foundMembers = membroEquipeRepository.findByEquipeProjetoId(equipeProjeto.getId());

        // Verifica se a lista não é nula, não está vazia, tem o tamanho esperado e contém os membros corretos.
        assertNotNull(foundMembers);
        assertFalse(foundMembers.isEmpty());
        assertEquals(2, foundMembers.size());
        assertTrue(foundMembers.contains(membro1));
        assertTrue(foundMembers.contains(membro2));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para equipe sem membros")
    void findByEquipeProjetoId_ShouldReturnEmptyList_WhenNoMembers() {
        // Define um ID de equipe que não existe.
        Long nonExistentEquipeId = 99L;
        // Configura o mock do repositório para retornar uma lista vazia quando findByEquipeProjetoId for chamado com um ID inexistente.
        when(membroEquipeRepository.findByEquipeProjetoId(nonExistentEquipeId)).thenReturn(Arrays.asList());

        // Executa o método do repositório.
        List<MembroEquipe> foundMembers = membroEquipeRepository.findByEquipeProjetoId(nonExistentEquipeId);

        // Verifica se a lista não é nula e está vazia.
        assertNotNull(foundMembers);
        assertTrue(foundMembers.isEmpty());
    }
}