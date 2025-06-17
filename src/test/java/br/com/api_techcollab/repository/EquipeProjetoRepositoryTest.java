package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.EquipeProjeto;
import br.com.api_techcollab.model.Projeto;
import br.com.api_techcollab.model.Empresa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EquipeProjetoRepositoryTest {

    @Mock // Cria um mock do EquipeProjetoRepository.
    private EquipeProjetoRepository equipeProjetoRepository;

    private Projeto projeto;
    private EquipeProjeto equipeProjeto;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        // Inicializa as entidades de teste.
        Empresa empresa = new Empresa();
        empresa.setId(1L);

        projeto = new Projeto();
        projeto.setId(10L);
        projeto.setTitulo("Projeto Alpha");
        projeto.setEmpresa(empresa);

        equipeProjeto = new EquipeProjeto();
        equipeProjeto.setId(1L);
        equipeProjeto.setProjeto(projeto);
        equipeProjeto.setNomeEquipe("Alpha Team");
        equipeProjeto.setDataFormacao(new Date());
    }

    @Test // Marca o método como um teste.
    @DisplayName("Deve encontrar uma equipe por ID de projeto") // Nome amigável para o teste.
    void findByProjetoId_ShouldReturnEquipe() {
        // Configura o mock do repositório para retornar a equipe de teste quando findByProjetoId for chamado com o ID do projeto.
        when(equipeProjetoRepository.findByProjetoId(projeto.getId())).thenReturn(Optional.of(equipeProjeto));

        // Executa o método do repositório.
        Optional<EquipeProjeto> foundEquipe = equipeProjetoRepository.findByProjetoId(projeto.getId());

        // Verifica se a equipe foi encontrada, se o nome da equipe e o ID do projeto correspondem.
        assertTrue(foundEquipe.isPresent());
        assertEquals(equipeProjeto.getNomeEquipe(), foundEquipe.get().getNomeEquipe());
        assertEquals(projeto.getId(), foundEquipe.get().getProjeto().getId());
    }

    @Test
    @DisplayName("Não deve encontrar equipe para ID de projeto inexistente")
    void findByProjetoId_ShouldReturnEmptyOptional_WhenNotFound() {
        // Define um ID de projeto que não existe.
        Long nonExistentProjetoId = 99L;
        // Configura o mock do repositório para retornar um Optional vazio quando findByProjetoId for chamado com o ID inexistente.
        when(equipeProjetoRepository.findByProjetoId(nonExistentProjetoId)).thenReturn(Optional.empty());

        // Executa o método do repositório.
        Optional<EquipeProjeto> foundEquipe = equipeProjetoRepository.findByProjetoId(nonExistentProjetoId);

        // Verifica se a equipe não foi encontrada.
        assertFalse(foundEquipe.isPresent());
    }
}