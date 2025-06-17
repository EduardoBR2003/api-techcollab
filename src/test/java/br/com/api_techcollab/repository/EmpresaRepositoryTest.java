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

    @Mock // Cria um mock do EmpresaRepository.
    private EmpresaRepository empresaRepository;

    private Empresa empresa;

    @BeforeEach // Executado antes de cada método de teste.
    void setUp() {
        // Inicializa uma entidade Empresa de teste.
        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNome("Empresa Teste SA");
        empresa.setEmail("contato@empresateste.com");
        empresa.setCnpj("00.000.000/0001-00");
        empresa.setRazaoSocial("Empresa Teste S.A.");
        empresa.setTipoUsuario(TiposUsuarios.EMPRESA);
        empresa.setDataCadastro(new Date());
    }

    @Test // Marca o método como um teste.
    @DisplayName("Deve encontrar uma empresa pelo CNPJ") // Nome amigável para o teste.
    void findByCnpj_ShouldReturnEmpresa() {
        // Define o CNPJ a ser buscado.
        String cnpj = "00.000.000/0001-00";
        // Configura o mock do repositório para retornar a empresa de teste quando findByCnpj for chamado com o CNPJ.
        when(empresaRepository.findByCnpj(cnpj)).thenReturn(Optional.of(empresa));

        // Executa o método do repositório.
        Optional<Empresa> foundEmpresa = empresaRepository.findByCnpj(cnpj);

        // Verifica se a empresa foi encontrada e se o CNPJ corresponde.
        assertTrue(foundEmpresa.isPresent());
        assertEquals(empresa.getCnpj(), foundEmpresa.get().getCnpj());
    }

    @Test
    @DisplayName("Não deve encontrar empresa para CNPJ inexistente")
    void findByCnpj_ShouldReturnEmptyOptional_WhenNotFound() {
        // Define um CNPJ que não existe.
        String nonExistentCnpj = "99.999.999/0001-99";
        // Configura o mock do repositório para retornar um Optional vazio quando findByCnpj for chamado com o CNPJ inexistente.
        when(empresaRepository.findByCnpj(nonExistentCnpj)).thenReturn(Optional.empty());

        // Executa o método do repositório.
        Optional<Empresa> foundEmpresa = empresaRepository.findByCnpj(nonExistentCnpj);

        // Verifica se a empresa não foi encontrada.
        assertFalse(foundEmpresa.isPresent());
    }
}