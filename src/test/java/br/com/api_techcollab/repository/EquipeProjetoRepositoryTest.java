// src/test/java/br/com/api_techcollab/repository/EquipeProjetoRepositoryTest.java
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

    @Mock
    private EquipeProjetoRepository equipeProjetoRepository;

    private Projeto projeto;
    private EquipeProjeto equipeProjeto;

    @BeforeEach
    void setUp() {
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

    @Test
    @DisplayName("Deve encontrar uma equipe por ID de projeto")
    void findByProjetoId_ShouldReturnEquipe() {
        // Arrange
        when(equipeProjetoRepository.findByProjetoId(projeto.getId())).thenReturn(Optional.of(equipeProjeto));

        // Act
        Optional<EquipeProjeto> foundEquipe = equipeProjetoRepository.findByProjetoId(projeto.getId());

        // Assert
        assertTrue(foundEquipe.isPresent());
        assertEquals(equipeProjeto.getNomeEquipe(), foundEquipe.get().getNomeEquipe());
        assertEquals(projeto.getId(), foundEquipe.get().getProjeto().getId());
    }

    @Test
    @DisplayName("Não deve encontrar equipe para ID de projeto inexistente")
    void findByProjetoId_ShouldReturnEmptyOptional_WhenNotFound() {
        // Arrange
        Long nonExistentProjetoId = 99L;
        when(equipeProjetoRepository.findByProjetoId(nonExistentProjetoId)).thenReturn(Optional.empty());

        // Act
        Optional<EquipeProjeto> foundEquipe = equipeProjetoRepository.findByProjetoId(nonExistentProjetoId);

        // Assert
        assertFalse(foundEquipe.isPresent());
    }
}