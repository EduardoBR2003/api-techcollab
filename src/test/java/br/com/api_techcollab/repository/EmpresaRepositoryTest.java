// src/test/java/br/com/api_techcollab/repository/EmpresaRepositoryTest.java
package br.com.api_techcollab.repository;

import br.com.api_techcollab.model.Empresa;
import br.com.api_techcollab.model.enums.TiposUsuarios;
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
public class EmpresaRepositoryTest {

    @Mock
    private EmpresaRepository empresaRepository;

    private Empresa empresa;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNome("Empresa Teste SA");
        empresa.setEmail("contato@empresateste.com");
        empresa.setCnpj("00.000.000/0001-00");
        empresa.setRazaoSocial("Empresa Teste S.A.");
        empresa.setTipoUsuario(TiposUsuarios.EMPRESA);
        empresa.setDataCadastro(new Date());
    }

    @Test
    @DisplayName("Deve encontrar uma empresa pelo CNPJ")
    void findByCnpj_ShouldReturnEmpresa() {
        // Arrange
        String cnpj = "00.000.000/0001-00";
        when(empresaRepository.findByCnpj(cnpj)).thenReturn(Optional.of(empresa));

        // Act
        Optional<Empresa> foundEmpresa = empresaRepository.findByCnpj(cnpj);

        // Assert
        assertTrue(foundEmpresa.isPresent());
        assertEquals(empresa.getCnpj(), foundEmpresa.get().getCnpj());
    }

    @Test
    @DisplayName("Não deve encontrar empresa para CNPJ inexistente")
    void findByCnpj_ShouldReturnEmptyOptional_WhenNotFound() {
        // Arrange
        String nonExistentCnpj = "99.999.999/0001-99";
        when(empresaRepository.findByCnpj(nonExistentCnpj)).thenReturn(Optional.empty());

        // Act
        Optional<Empresa> foundEmpresa = empresaRepository.findByCnpj(nonExistentCnpj);

        // Assert
        assertFalse(foundEmpresa.isPresent());
    }
}