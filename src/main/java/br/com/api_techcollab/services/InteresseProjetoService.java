package br.com.api_techcollab.services;

import br.com.api_techcollab.controller.EmpresaController;
import br.com.api_techcollab.controller.InteresseProjetoController;
import br.com.api_techcollab.controller.ProfissionalController;
import br.com.api_techcollab.controller.ProjetoController;
import br.com.api_techcollab.dto.*;
import br.com.api_techcollab.exceptions.AccessDeniedException;
import br.com.api_techcollab.exceptions.BusinessException;
import br.com.api_techcollab.exceptions.ResourceNotFoundException;
import br.com.api_techcollab.model.InteresseProjeto;
import br.com.api_techcollab.model.Profissional;
import br.com.api_techcollab.model.Projeto;
import br.com.api_techcollab.model.VagaProjeto;
import br.com.api_techcollab.model.enums.StatusInteresse;
import br.com.api_techcollab.model.enums.StatusProjeto;
import br.com.api_techcollab.repository.InteresseProjetoRepository;
import br.com.api_techcollab.repository.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class InteresseProjetoService {

    private final Logger logger = Logger.getLogger(InteresseProjetoService.class.getName());

    @Autowired
    private InteresseProjetoRepository interesseProjetoRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private VagaProjetoService vagaProjetoService; // Para buscar entidade VagaProjeto

    @Autowired
    private EquipeProjetoService equipeProjetoService; // Para adicionar à equipe

    @Transactional
    public InteresseProjetoResponseDTO manifestarInteresse(Long profissionalId, InteresseCreateDTO dto) {
        logger.info("Profissional ID: " + profissionalId + " manifestando interesse na vaga ID: " + dto.getVagaProjetoId());
        Profissional profissional = profissionalRepository.findById(profissionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID: " + profissionalId));

        VagaProjeto vaga = vagaProjetoService.buscarVagaPorIdEntidade(dto.getVagaProjetoId());

        if (vaga.getProjeto().getStatusProjeto() != StatusProjeto.ABERTO_PARA_INTERESSE) {
            throw new BusinessException("Não é possível manifestar interesse. Projeto não está aberto para interesse.");
        }

        boolean jaInteressado = interesseProjetoRepository.findByProfissionalId(profissionalId).stream()
                .anyMatch(i -> i.getVagaProjeto().getId().equals(vaga.getId()));
        if (jaInteressado) {
            throw new BusinessException("Profissional já manifestou interesse nesta vaga.");
        }

        InteresseProjeto interesse = new InteresseProjeto();
        interesse.setProfissional(profissional);
        interesse.setVagaProjeto(vaga);
        interesse.setMensagemMotivacao(dto.getMensagemMotivacao());
        interesse.setStatusInteresse(StatusInteresse.PENDENTE);

        InteresseProjeto savedInteresse = interesseProjetoRepository.save(interesse);
        logger.info("Interesse ID " + savedInteresse.getId() + " criado com sucesso.");
        return toResponseDTOWithLinks(savedInteresse);
    }

    public List<InteresseProjetoResponseDTO> visualizarInteressadosPorVaga(Long vagaId, Long empresaIdAutenticada) {
        logger.info("Visualizando interessados na vaga ID: " + vagaId + " pela empresa ID: " + empresaIdAutenticada);
        VagaProjeto vaga = vagaProjetoService.buscarVagaPorIdEntidade(vagaId);

        if (!vaga.getProjeto().getEmpresa().getId().equals(empresaIdAutenticada)) {
            throw new AccessDeniedException("Empresa não autorizada a visualizar interessados desta vaga.");
        }

        List<InteresseProjeto> interesses = interesseProjetoRepository.findByVagaProjetoId(vagaId);
        return interesses.stream().map(this::toResponseDTOWithLinks).collect(Collectors.toList()); // Chama o método ajustado
    }

    @Transactional
    public InteresseProjetoResponseDTO atualizarStatusInteresseEmpresa(Long interesseId, InteresseStatusUpdateDTO dto, Long empresaIdAutenticada) {
        logger.info("Empresa ID: " + empresaIdAutenticada + " atualizando status do interesse ID: " + interesseId + " para " + dto.getStatusInteresse());
        InteresseProjeto interesse = interesseProjetoRepository.findById(interesseId)
                .orElseThrow(() -> new ResourceNotFoundException("Interesse não encontrado com ID: " + interesseId));

        if (!interesse.getVagaProjeto().getProjeto().getEmpresa().getId().equals(empresaIdAutenticada)) {
            throw new AccessDeniedException("Empresa não autorizada a atualizar este interesse.");
        }

        if (interesse.getStatusInteresse() != StatusInteresse.PENDENTE && interesse.getStatusInteresse() != StatusInteresse.RECUSADO_DO_PROF) {
            throw new BusinessException("Interesse só pode ser atualizado pela empresa se estiver PENDENTE ou RECUSADO_DO_PROF. Status atual: " + interesse.getStatusInteresse());
        }
        if (dto.getStatusInteresse() != StatusInteresse.SELECIONADO && dto.getStatusInteresse() != StatusInteresse.RECUSADO_PELA_EMPRESA) {
            throw new BusinessException("Empresa só pode alterar status para SELECIONADO ou RECUSADO_PELA_EMPRESA.");
        }

        interesse.setStatusInteresse(dto.getStatusInteresse());
        InteresseProjeto updatedInteresse = interesseProjetoRepository.save(interesse);
        logger.info("Status do interesse ID " + interesseId + " atualizado para " + dto.getStatusInteresse());
        return toResponseDTOWithLinks(updatedInteresse); // Chama o método ajustado
    }

    @Transactional
    public InteresseProjetoResponseDTO profissionalResponderAlocacao(Long interesseId, InteresseStatusUpdateDTO dto, Long profissionalIdAutenticado) {
        logger.info("Profissional ID: " + profissionalIdAutenticado + " respondendo alocação para o interesse ID: " + interesseId + " com status " + dto.getStatusInteresse());
        InteresseProjeto interesse = interesseProjetoRepository.findById(interesseId)
                .orElseThrow(() -> new ResourceNotFoundException("Interesse não encontrado com ID: " + interesseId));

        if (!interesse.getProfissional().getId().equals(profissionalIdAutenticado)) {
            throw new AccessDeniedException("Profissional não autorizado a responder por este interesse.");
        }

        if (interesse.getStatusInteresse() != StatusInteresse.SELECIONADO) {
            throw new BusinessException("Profissional só pode responder se o interesse estiver SELECIONADO. Status atual: " + interesse.getStatusInteresse());
        }
        if (dto.getStatusInteresse() != StatusInteresse.ALOCADO && dto.getStatusInteresse() != StatusInteresse.RECUSADO_DO_PROF) {
            throw new BusinessException("Profissional só pode alterar status para ALOCADO ou RECUSADO_DO_PROF.");
        }

        interesse.setStatusInteresse(dto.getStatusInteresse());
        InteresseProjeto updatedInteresse = interesseProjetoRepository.save(interesse);

        if (dto.getStatusInteresse() == StatusInteresse.ALOCADO) {
            logger.info("Interesse ID " + interesseId + " ACEITO. Adicionando profissional à equipe do projeto ID: " + interesse.getVagaProjeto().getProjeto().getId());
            equipeProjetoService.adicionarMembroEquipePorInteresse(interesse);
        } else {
            logger.info("Interesse ID " + interesseId + " RECUSADO pelo profissional.");
        }
        return toResponseDTOWithLinks(updatedInteresse); // Chama o método ajustado
    }

    public List<InteresseProjetoResponseDTO> consultarStatusInteressesProfissional(Long profissionalId) {
        logger.info("Consultando status de interesses para o profissional ID: " + profissionalId);
        if (!profissionalRepository.existsById(profissionalId)) {
            throw new ResourceNotFoundException("Profissional não encontrado com ID: " + profissionalId);
        }
        List<InteresseProjeto> interesses = interesseProjetoRepository.findByProfissionalId(profissionalId);
        return interesses.stream().map(this::toResponseDTOWithLinks).collect(Collectors.toList()); // Chama o método ajustado
    }

    private InteresseProjetoResponseDTO toResponseDTOWithLinks(InteresseProjeto interesse) {
        InteresseProjetoResponseDTO dto = new InteresseProjetoResponseDTO();

        dto.setId(interesse.getId());
        dto.setStatusInteresse(interesse.getStatusInteresse());
        dto.setMensagemMotivacao(interesse.getMensagemMotivacao());

        Profissional prof = interesse.getProfissional();
        VagaProjeto vaga = interesse.getVagaProjeto();
        Projeto proj = vaga.getProjeto();
        Long empresaId = proj.getEmpresa().getId();

        if (prof != null) {
            dto.setProfissional(new ProfissionalSimpleResponseDTO(prof.getId(), prof.getNome(), prof.getEmail()));
            dto.getLinks().add(new CustomLink("profissional", linkTo(methodOn(ProfissionalController.class).findById(prof.getId())).withSelfRel().getHref(), "GET"));
        }

        if (vaga != null) {
            dto.setVagaProjeto(new VagaProjetoSimpleResponseDTO(vaga.getId(), vaga.getTituloVaga(), proj.getId()));
        }

        if (proj != null) {
            dto.setProjeto(new ProjetoSimpleResponseDTO(proj.getId(), proj.getTitulo()));
            dto.getLinks().add(new CustomLink("projeto", linkTo(methodOn(ProjetoController.class).buscarProjetoPorId(proj.getId())).withSelfRel().getHref(), "GET"));
        }

        // Links de Ação baseados no Status

        if (interesse.getStatusInteresse() == StatusInteresse.SELECIONADO) {
            dto.getLinks().add(new CustomLink("profissional_responder", linkTo(methodOn(InteresseProjetoController.class).profissionalResponderAlocacao(prof.getId(), interesse.getId(), null)).withSelfRel().getHref(), "PUT"));
        }

        return dto;
    }
}