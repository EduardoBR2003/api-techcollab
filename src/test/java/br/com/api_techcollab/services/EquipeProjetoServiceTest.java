// src/test/java/br/com/api_techcollab/services/EquipeProjetoServiceTest.java
package br.com.api_techcollab.services;

import br.com.api_techcollab.dto.EquipeMontarDTO;
import br.com.api_techcollab.dto.EquipeProjetoResponseDTO;
import br.com.api_techcollab.exceptions.AccessDeniedException;
import br.com.api_techcollab.exceptions.BusinessException;
import br.com.api_techcollab.exceptions.ResourceNotFoundException;
import br.com.api_techcollab.model.*;
import br.com.api_techcollab.model.enums.StatusInteresse;
import br.com.api_techcollab.model.enums.StatusProjeto;
import br.com.api_techcollab.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EquipeProjetoServiceTest {

    @Mock
    private EquipeProjetoRepository equipeProjetoRepository;
    @Mock
    private MembroEquipeRepository membroEquipeRepository;
    @Mock
    private ProjetoRepository projetoRepository;
    @Mock
    private ProjetoService projetoService; // Mockar o serviço para não testar recursivamente
    @Mock
    private InteresseProjetoRepository interesseProjetoRepository;
    @Mock
    private VagaProjetoRepository vagaProjetoRepository;

    @InjectMocks
    private EquipeProjetoService equipeProjetoService;

    private Empresa empresa;
    private Projeto projeto;
    private EquipeProjeto equipeProjeto;
    private Profissional profissional1;
    private Profissional profissional2;
    private VagaProjeto vagaProjeto1;
    private VagaProjeto vagaProjeto2;
    private InteresseProjeto interesse1;
    private InteresseProjeto interesse2;
    private MembroEquipe membroEquipe;
    private EquipeMontarDTO equipeMontarDTO;

    @BeforeEach
    void setUp() {
        // Entidades base
        empresa = new Empresa();
        empresa.setId(1L);

        projeto = new Projeto();
        projeto.setId(10L);
        projeto.setEmpresa(empresa);
        projeto.setStatusProjeto(StatusProjeto.ABERTO_PARA_INTERESSE);

        equipeProjeto = new EquipeProjeto();
        equipeProjeto.setId(1L);
        equipeProjeto.setProjeto(projeto);
        equipeProjeto.setNomeEquipe("Equipe Inicial");

        profissional1 = new Profissional();
        profissional1.setId(100L);
        profissional1.setNome("Prof 1");

        profissional2 = new Profissional();
        profissional2.setId(101L);
        profissional2.setNome("Prof 2");

        vagaProjeto1 = new VagaProjeto();
        vagaProjeto1.setId(1000L);
        vagaProjeto1.setProjeto(projeto);
        vagaProjeto1.setTituloVaga("Dev Backend");
        vagaProjeto1.setQuantFuncionarios(1);

        vagaProjeto2 = new VagaProjeto();
        vagaProjeto2.setId(1001L);
        vagaProjeto2.setProjeto(projeto);
        vagaProjeto2.setTituloVaga("Dev Frontend");
        vagaProjeto2.setQuantFuncionarios(1);


        interesse1 = new InteresseProjeto();
        interesse1.setId(10L);
        interesse1.setProfissional(profissional1);
        interesse1.setVagaProjeto(vagaProjeto1);
        interesse1.setStatusInteresse(StatusInteresse.SELECIONADO);

        interesse2 = new InteresseProjeto();
        interesse2.setId(11L);
        interesse2.setProfissional(profissional2);
        interesse2.setVagaProjeto(vagaProjeto2);
        interesse2.setStatusInteresse(StatusInteresse.SELECIONADO);

        membroEquipe = new MembroEquipe();
        membroEquipe.setId(20L);
        membroEquipe.setEquipeProjeto(equipeProjeto);
        membroEquipe.setProfissional(profissional1);
        membroEquipe.setPapel("Desenvolvedor");

        equipeMontarDTO = new EquipeMontarDTO();
        equipeMontarDTO.setEmpresaId(empresa.getId());
        equipeMontarDTO.setIdsInteressesSelecionados(Arrays.asList(interesse1.getId(), interesse2.getId()));
        equipeMontarDTO.setNomeEquipeSugerido("Nova Equipe Top");
    }

    @Test
    @DisplayName("Deve montar equipe com sucesso, criando nova equipe")
    void montarEquipe_NewTeam_Success() {
        when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));
        when(equipeProjetoRepository.findByProjetoId(projeto.getId())).thenReturn(Optional.empty()); // Nenhuma equipe existente
        when(equipeProjetoRepository.save(any(EquipeProjeto.class))).thenReturn(equipeProjeto);
        when(interesseProjetoRepository.findById(interesse1.getId())).thenReturn(Optional.of(interesse1));
        when(interesseProjetoRepository.findById(interesse2.getId())).thenReturn(Optional.of(interesse2));
        when(membroEquipeRepository.findByEquipeProjetoId(anyLong())).thenReturn(Collections.emptyList()); // Nenhum membro ainda
        when(membroEquipeRepository.save(any(MembroEquipe.class))).thenReturn(membroEquipe); // Salva o membro
        when(interesseProjetoRepository.save(any(InteresseProjeto.class))).thenReturn(interesse1); // Atualiza o interesse
        doNothing().when(projetoService).atualizarStatusProjeto(anyLong(), any(StatusProjeto.class));
        when(vagaProjetoRepository.findByProjetoId(projeto.getId())).thenReturn(Arrays.asList(vagaProjeto1, vagaProjeto2));
        when(membroEquipeRepository.findByEquipeProjetoId(equipeProjeto.getId())).thenReturn(Arrays.asList(membroEquipe)); // Retorna membros após adição (simplificado)

        EquipeProjetoResponseDTO response = equipeProjetoService.montarEquipe(
                projeto.getId(), equipeMontarDTO.getIdsInteressesSelecionados(),
                equipeMontarDTO.getNomeEquipeSugerido(), equipeMontarDTO.getEmpresaId());

        assertNotNull(response);
        assertEquals("Nova Equipe Top", response.getNomeEquipe());
        verify(equipeProjetoRepository, times(1)).save(any(EquipeProjeto.class)); // Nova equipe criada
        verify(membroEquipeRepository, times(2)).save(any(MembroEquipe.class)); // 2 membros adicionados
        verify(interesseProjetoRepository, times(2)).save(any(InteresseProjeto.class)); // Interesses atualizados
        verify(projetoService, times(2)).atualizarStatusProjeto(eq(projeto.getId()), any(StatusProjeto.class)); // Status do projeto atualizado
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao montar equipe para projeto não encontrado")
    void montarEquipe_ProjetoNotFound_ThrowsException() {
        when(projetoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                equipeProjetoService.montarEquipe(999L, equipeMontarDTO.getIdsInteressesSelecionados(),
                        equipeMontarDTO.getNomeEquipeSugerido(), equipeMontarDTO.getEmpresaId()));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao empresa não autorizada tentar montar equipe")
    void montarEquipe_AccessDenied_ThrowsException() {
        Empresa outraEmpresa = new Empresa();
        outraEmpresa.setId(99L);
        projeto.setEmpresa(outraEmpresa); // Projeto pertence a outra empresa

        when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));

        assertThrows(AccessDeniedException.class, () ->
                equipeProjetoService.montarEquipe(projeto.getId(), equipeMontarDTO.getIdsInteressesSelecionados(),
                        equipeMontarDTO.getNomeEquipeSugerido(), empresa.getId())); // Empresa errada tenta montar
    }

    @Test
    @DisplayName("Deve adicionar membro à equipe via interesse (método interno)")
    void adicionarMembroEquipePorInteresse_Success() {
        interesse1.setStatusInteresse(StatusInteresse.ALOCADO); // Interesse já alocado
        when(equipeProjetoRepository.findByProjetoId(projeto.getId())).thenReturn(Optional.of(equipeProjeto));
        when(membroEquipeRepository.findByEquipeProjetoId(equipeProjeto.getId())).thenReturn(Collections.emptyList()); // Membro não existe
        when(membroEquipeRepository.save(any(MembroEquipe.class))).thenReturn(membroEquipe);
        doNothing().when(projetoService).atualizarStatusProjeto(anyLong(), any(StatusProjeto.class));
        when(vagaProjetoRepository.findByProjetoId(projeto.getId())).thenReturn(Arrays.asList(vagaProjeto1, vagaProjeto2));
        when(membroEquipeRepository.findByEquipeProjetoId(equipeProjeto.getId())).thenReturn(Arrays.asList(membroEquipe)); // Simula membro adicionado

        equipeProjetoService.adicionarMembroEquipePorInteresse(interesse1);

        verify(membroEquipeRepository, times(1)).save(any(MembroEquipe.class));
        verify(projetoService, times(2)).atualizarStatusProjeto(eq(projeto.getId()), any(StatusProjeto.class));
    }

    @Test
    @DisplayName("Não deve adicionar membro se interesse não estiver ALOCADO")
    void adicionarMembroEquipePorInteresse_NotAllocated_ThrowsException() {
        interesse1.setStatusInteresse(StatusInteresse.SELECIONADO); // Não é ALOCADO

        assertThrows(BusinessException.class, () ->
                equipeProjetoService.adicionarMembroEquipePorInteresse(interesse1));
        verify(membroEquipeRepository, never()).save(any(MembroEquipe.class));
    }

    @Test
    @DisplayName("Deve visualizar equipe de projeto existente")
    void visualizarEquipeProjeto_ExistingTeam_Success() {
        when(equipeProjetoRepository.findByProjetoId(projeto.getId())).thenReturn(Optional.of(equipeProjeto));
        when(membroEquipeRepository.findByEquipeProjetoId(equipeProjeto.getId())).thenReturn(Collections.singletonList(membroEquipe));

        EquipeProjetoResponseDTO response = equipeProjetoService.visualizarEquipeProjeto(projeto.getId());

        assertNotNull(response);
        assertEquals(equipeProjeto.getId(), response.getId());
        assertFalse(response.getMembros().isEmpty());
        assertEquals(1, response.getMembros().size());
    }

    @Test
    @DisplayName("Deve visualizar equipe de projeto não formada, retornando placeholder")
    void visualizarEquipeProjeto_NoTeam_ReturnsPlaceholder() {
        when(equipeProjetoRepository.findByProjetoId(projeto.getId())).thenReturn(Optional.empty());
        when(projetoRepository.existsById(projeto.getId())).thenReturn(true); // Projeto existe
        when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto)); // Usado para o placeholder

        EquipeProjetoResponseDTO response = equipeProjetoService.visualizarEquipeProjeto(projeto.getId());

        assertNotNull(response);
        assertNull(response.getId()); // ID da equipe deve ser nulo para placeholder
        assertEquals("Equipe não formada", response.getNomeEquipe());
        assertTrue(response.getMembros().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao visualizar equipe de projeto inexistente")
    void visualizarEquipeProjeto_ProjetoNotFound_ThrowsException() {
        when(equipeProjetoRepository.findByProjetoId(anyLong())).thenReturn(Optional.empty());
        when(projetoRepository.existsById(anyLong())).thenReturn(false); // Projeto não existe

        assertThrows(ResourceNotFoundException.class, () ->
                equipeProjetoService.visualizarEquipeProjeto(999L));
    }
}