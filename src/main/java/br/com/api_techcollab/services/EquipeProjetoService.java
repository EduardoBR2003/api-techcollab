package br.com.api_techcollab.services;

import br.com.api_techcollab.controller.EquipeProjetoController;
import br.com.api_techcollab.controller.ProjetoController;
import br.com.api_techcollab.dto.CustomLink;
import br.com.api_techcollab.dto.EquipeProjetoResponseDTO;
import br.com.api_techcollab.dto.MembroEquipeResponseDTO;
import br.com.api_techcollab.dto.ProfissionalSimpleResponseDTO;
import br.com.api_techcollab.exceptions.AccessDeniedException;
import br.com.api_techcollab.exceptions.BusinessException;
import br.com.api_techcollab.exceptions.ResourceNotFoundException;
import br.com.api_techcollab.mapper.DataMapper;
import br.com.api_techcollab.model.*;
import br.com.api_techcollab.model.enums.StatusInteresse;
import br.com.api_techcollab.model.enums.StatusProjeto;
import br.com.api_techcollab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class EquipeProjetoService {

    private final Logger logger = Logger.getLogger(EquipeProjetoService.class.getName());

    @Autowired
    private EquipeProjetoRepository equipeProjetoRepository;

    @Autowired
    private MembroEquipeRepository membroEquipeRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private ProjetoService projetoService; // Para atualizar status do projeto

    @Autowired
    private InteresseProjetoRepository interesseProjetoRepository;

    @Autowired
    private VagaProjetoRepository vagaProjetoRepository;


    // Método para mapear para EquipeProjetoResponseDTO
    private EquipeProjetoResponseDTO toResponseDTOWithLinks(EquipeProjeto equipe, List<MembroEquipe> membros) {
        EquipeProjetoResponseDTO dto = DataMapper.parseObject(equipe, EquipeProjetoResponseDTO.class);
        if (equipe.getProjeto() != null) {
            dto.setProjetoId(equipe.getProjeto().getId());
        }
        if (membros != null) {
            dto.setMembros(membros.stream().map(membro -> {
                MembroEquipeResponseDTO membroDto = DataMapper.parseObject(membro, MembroEquipeResponseDTO.class);
                if (membro.getProfissional() != null) {
                    Profissional prof = membro.getProfissional();
                    membroDto.setProfissional(new ProfissionalSimpleResponseDTO(prof.getId(), prof.getNome(), prof.getEmail()));
                    // Adicionar link para o perfil do profissional dentro do membro da equipe
                    // membroDto.getLinks().add(new CustomLink("perfil_profissional", ...)); // (Opcional, se MembroEquipeResponseDTO tiver a lista de links)
                }
                return membroDto;
            }).collect(Collectors.toList()));
        } else {
            dto.setMembros(Collections.emptyList());
        }

        // Adicionando links ao DTO principal da equipe
        Long projetoId = equipe.getProjeto().getId();
        dto.getLinks().add(new CustomLink("self", linkTo(methodOn(EquipeProjetoController.class).visualizarEquipeProjeto(projetoId)).withSelfRel().getHref(), "GET"));
        dto.getLinks().add(new CustomLink("projeto", linkTo(methodOn(ProjetoController.class).buscarProjetoPorId(projetoId)).withSelfRel().getHref(), "GET"));
        dto.getLinks().add(new CustomLink("montar_equipe", linkTo(methodOn(EquipeProjetoController.class).montarEquipe(projetoId, null)).withSelfRel().getHref(), "POST"));

        return dto;
    }


    @Transactional
    public EquipeProjetoResponseDTO montarEquipe(Long projetoId, List<Long> idsInteressesSelecionados, String nomeEquipeSugerido, Long empresaIdAutenticada) {
        logger.info("Montando equipe para projeto ID: " + projetoId + " pela empresa ID: " + empresaIdAutenticada);
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com ID: " + projetoId));

        if (!projeto.getEmpresa().getId().equals(empresaIdAutenticada)) {
            throw new AccessDeniedException("Empresa não autorizada a montar equipe para este projeto.");
        }

        if (projeto.getStatusProjeto() != StatusProjeto.ABERTO_PARA_INTERESSE && projeto.getStatusProjeto() != StatusProjeto.EM_FORMACAO_EQUIPE) {
            throw new BusinessException("Projeto não está em status que permita formação de equipe. Status atual: " + projeto.getStatusProjeto());
        }

        // Busca ou cria a EquipeProjeto
        EquipeProjeto equipe = equipeProjetoRepository.findByProjetoId(projetoId)
                .orElseGet(() -> {
                    EquipeProjeto novaEquipe = new EquipeProjeto();
                    novaEquipe.setProjeto(projeto);
                    novaEquipe.setNomeEquipe(nomeEquipeSugerido != null ? nomeEquipeSugerido : "Equipe " + projeto.getTitulo());
                    novaEquipe.setDataFormacao(new Date());
                    return equipeProjetoRepository.save(novaEquipe);
                });

        for (Long interesseId : idsInteressesSelecionados) {
            InteresseProjeto interesse = interesseProjetoRepository.findById(interesseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Interesse ID " + interesseId + " não encontrado."));

            if (interesse.getStatusInteresse() != StatusInteresse.SELECIONADO) {
                logger.warning("Interesse ID " + interesseId + " não está com status SELECIONADO, mas sim " + interesse.getStatusInteresse() + ". Não será adicionado à equipe.");
                continue; // Ou lançar exceção
            }

            // Verifica se o membro já existe para evitar duplicidade
            boolean membroJaExiste = membroEquipeRepository.findByEquipeProjetoId(equipe.getId()).stream()
                    .anyMatch(m -> m.getProfissional().getId().equals(interesse.getProfissional().getId()));

            if(membroJaExiste) {
                logger.info("Profissional ID " + interesse.getProfissional().getId() + " já é membro da equipe do projeto ID " + projetoId);
                interesse.setStatusInteresse(StatusInteresse.ALOCADO); // Garante o status correto
                interesseProjetoRepository.save(interesse);
                continue;
            }

            MembroEquipe novoMembro = new MembroEquipe();
            novoMembro.setEquipeProjeto(equipe);
            novoMembro.setProfissional(interesse.getProfissional());
            novoMembro.setPapel(interesse.getVagaProjeto().getTituloVaga()); // Papel pode vir da vaga
            novoMembro.setDataEntrada(new Date());
            membroEquipeRepository.save(novoMembro);

            interesse.setStatusInteresse(StatusInteresse.ALOCADO);
            interesseProjetoRepository.save(interesse);
            logger.info("Profissional ID " + novoMembro.getProfissional().getId() + " adicionado à equipe do projeto ID " + projetoId);
        }

        // Atualizar status do projeto
        projetoService.atualizarStatusProjeto(projetoId, StatusProjeto.EM_FORMACAO_EQUIPE); // ou DESENVOLVIMENTO se equipe completa
        verificarCompletudeEquipeEAtualizarProjeto(projetoId);

        List<MembroEquipe> membros = membroEquipeRepository.findByEquipeProjetoId(equipe.getId());
        return toResponseDTOWithLinks(equipe, membros);
    }

    // Método interno chamado pelo InteresseProjetoService quando um profissional aceita
    @Transactional
    public void adicionarMembroEquipePorInteresse(InteresseProjeto interesse) {
        if (interesse.getStatusInteresse() != StatusInteresse.ALOCADO) {
            throw new BusinessException("Interesse não está com status ALOCADO para adicionar membro à equipe.");
        }

        Projeto projeto = interesse.getVagaProjeto().getProjeto();
        logger.info("Adicionando membro à equipe do projeto ID " + projeto.getId() + " via interesse ID " + interesse.getId());

        EquipeProjeto equipe = equipeProjetoRepository.findByProjetoId(projeto.getId())
                .orElseGet(() -> {
                    EquipeProjeto novaEquipe = new EquipeProjeto();
                    novaEquipe.setProjeto(projeto);
                    novaEquipe.setNomeEquipe("Equipe " + projeto.getTitulo());
                    novaEquipe.setDataFormacao(new Date());
                    return equipeProjetoRepository.save(novaEquipe);
                });

        // Verifica se o membro já existe
        boolean membroJaExiste = membroEquipeRepository.findByEquipeProjetoId(equipe.getId()).stream()
                .anyMatch(m -> m.getProfissional().getId().equals(interesse.getProfissional().getId()));

        if(!membroJaExiste) {
            MembroEquipe novoMembro = new MembroEquipe();
            novoMembro.setEquipeProjeto(equipe);
            novoMembro.setProfissional(interesse.getProfissional());
            novoMembro.setPapel(interesse.getVagaProjeto().getTituloVaga());
            novoMembro.setDataEntrada(new Date());
            membroEquipeRepository.save(novoMembro);
            logger.info("Profissional ID " + novoMembro.getProfissional().getId() + " adicionado à equipe via aceite de interesse.");
        } else {
            logger.info("Profissional ID " + interesse.getProfissional().getId() + " já era membro da equipe.");
        }

        projetoService.atualizarStatusProjeto(projeto.getId(), StatusProjeto.EM_FORMACAO_EQUIPE);
        verificarCompletudeEquipeEAtualizarProjeto(projeto.getId());
    }

    @Transactional
    public void verificarCompletudeEquipeEAtualizarProjeto(Long projetoId) {
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado: " + projetoId));

        List<VagaProjeto> vagasDoProjeto = vagaProjetoRepository.findByProjetoId(projetoId);
        if (vagasDoProjeto.isEmpty() && projeto.getStatusProjeto() == StatusProjeto.ABERTO_PARA_INTERESSE) {
            // Se não há vagas definidas, não há como formar equipe. Mantém status ou empresa define manualmente.
            logger.info("Projeto ID " + projetoId + " não possui vagas definidas. Status não alterado para DESENVOLVIMENTO.");
            return;
        }

        int totalVagasNecessarias = vagasDoProjeto.stream().mapToInt(VagaProjeto::getQuantFuncionarios).sum();

        Optional<EquipeProjeto> equipeOpt = equipeProjetoRepository.findByProjetoId(projetoId);
        if(equipeOpt.isEmpty() && totalVagasNecessarias > 0){
            logger.info("Projeto ID " + projetoId + " ainda não possui equipe formada. Status não alterado.");
            return;
        }

        long membrosAlocados = 0;
        if(equipeOpt.isPresent()){
            membrosAlocados = membroEquipeRepository.findByEquipeProjetoId(equipeOpt.get().getId()).size();
        }


        if (totalVagasNecessarias > 0 && membrosAlocados >= totalVagasNecessarias) {
            if(projeto.getStatusProjeto() == StatusProjeto.EM_FORMACAO_EQUIPE || projeto.getStatusProjeto() == StatusProjeto.ABERTO_PARA_INTERESSE) {
                projetoService.atualizarStatusProjeto(projetoId, StatusProjeto.DESENVOLVIMENTO);
                logger.info("Equipe do Projeto ID " + projetoId + " está completa. Status atualizado para DESENVOLVIMENTO.");
            }
        } else {
            if (projeto.getStatusProjeto() == StatusProjeto.DESENVOLVIMENTO && membrosAlocados < totalVagasNecessarias) {
                // Se por algum motivo um membro saiu e a equipe ficou incompleta, volta para formação
                projetoService.atualizarStatusProjeto(projetoId, StatusProjeto.EM_FORMACAO_EQUIPE);
                logger.info("Equipe do Projeto ID " + projetoId + " ficou incompleta. Status atualizado para EM_FORMACAO_EQUIPE.");
            } else if (projeto.getStatusProjeto() == StatusProjeto.ABERTO_PARA_INTERESSE && membrosAlocados > 0) {
                // Se já tem membros mas não está completa, e ainda estava aberto para interesse
                projetoService.atualizarStatusProjeto(projetoId, StatusProjeto.EM_FORMACAO_EQUIPE);
                logger.info("Projeto ID " + projetoId + " iniciou formação de equipe. Status EM_FORMACAO_EQUIPE.");
            }
        }
    }


    public EquipeProjetoResponseDTO visualizarEquipeProjeto(Long projetoId) {
        logger.info("Visualizando equipe do projeto ID: " + projetoId);
        Optional<EquipeProjeto> equipeOpt = equipeProjetoRepository.findByProjetoId(projetoId);

        if (equipeOpt.isEmpty()) {
            if(!projetoRepository.existsById(projetoId)){
                throw new ResourceNotFoundException("Projeto não encontrado com ID: " + projetoId);
            }
            logger.info("Nenhuma equipe formada para o projeto ID: " + projetoId);
            Projeto projeto = projetoRepository.findById(projetoId).get();
            EquipeProjeto equipePlaceholder = new EquipeProjeto();
            equipePlaceholder.setProjeto(projeto);
            equipePlaceholder.setNomeEquipe("Equipe não formada");
            return toResponseDTOWithLinks(equipePlaceholder, Collections.emptyList());
        }

        EquipeProjeto equipe = equipeOpt.get();
        List<MembroEquipe> membros = membroEquipeRepository.findByEquipeProjetoId(equipe.getId());
        return toResponseDTOWithLinks(equipe, membros);
    }
}