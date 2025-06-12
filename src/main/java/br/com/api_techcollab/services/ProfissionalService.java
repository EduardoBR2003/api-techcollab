package br.com.api_techcollab.services;

import br.com.api_techcollab.controller.InteresseProjetoController;
import br.com.api_techcollab.controller.ProfissionalController;
import br.com.api_techcollab.controller.ProjetoController;
import br.com.api_techcollab.dto.CustomLink;
import br.com.api_techcollab.dto.ProfissionalCreateDTO;
import br.com.api_techcollab.dto.ProfissionalResponseDTO;
import br.com.api_techcollab.exceptions.BusinessException;
import br.com.api_techcollab.exceptions.ResourceNotFoundException;
import br.com.api_techcollab.mapper.DataMapper;
import br.com.api_techcollab.model.Profissional;
import br.com.api_techcollab.model.enums.StatusInteresse;
import br.com.api_techcollab.repository.InteresseProjetoRepository;
import br.com.api_techcollab.repository.MembroEquipeRepository;
import br.com.api_techcollab.repository.ProfissionalRepository;
import br.com.api_techcollab.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ProfissionalService {

    private final Logger logger = Logger.getLogger(ProfissionalService.class.getName());

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MembroEquipeRepository membroEquipeRepository;

    @Autowired
    private InteresseProjetoRepository interesseProjetoRepository;

    @Cacheable("profissionaisFindAll")
    public List<ProfissionalResponseDTO> findAll() {
        logger.info("Buscando todos os profissionais...");
        var profissionais = profissionalRepository.findAll();
        var dtos = DataMapper.parseListObjects(profissionais, ProfissionalResponseDTO.class);

        // MODIFICAÇÃO: Adicionar link 'self' a cada profissional na lista
        dtos.forEach(dto -> {
            try {
                dto.getLinks().add(new CustomLink("self", linkTo(methodOn(ProfissionalController.class).findById(dto.getId())).withSelfRel().getHref(), "GET"));
            } catch (Exception e) {
                logger.warning("Erro ao gerar link HATEOAS para o profissional ID: " + dto.getId());
            }
        });

        return dtos;
    }

    @Cacheable(value = "profissionaisFindById", key = "#id")
    public ProfissionalResponseDTO findById(Long id) {
        logger.info("Buscando profissional pelo ID: " + id);
        var entity = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com o ID: " + id));

        ProfissionalResponseDTO dto = DataMapper.parseObject(entity, ProfissionalResponseDTO.class);

        // MODIFICAÇÃO: Lógica HATEOAS Customizada
        dto.getLinks().add(new CustomLink("self", linkTo(methodOn(ProfissionalController.class).findById(id)).withSelfRel().getHref(), "GET"));
        dto.getLinks().add(new CustomLink("update", linkTo(methodOn(ProfissionalController.class).update(id, null)).withSelfRel().getHref(), "PUT"));
        dto.getLinks().add(new CustomLink("delete", linkTo(methodOn(ProfissionalController.class).delete(id)).withSelfRel().getHref(), "DELETE"));
        dto.getLinks().add(new CustomLink("meus_interesses", linkTo(methodOn(InteresseProjetoController.class).consultarStatusInteressesProfissional(id)).withSelfRel().getHref(), "GET"));
        dto.getLinks().add(new CustomLink("projetos_disponiveis", linkTo(methodOn(ProjetoController.class).consultarProjetosDisponiveis()).withSelfRel().getHref(), "GET"));
        dto.getLinks().add(new CustomLink("todos_os_profissionais", linkTo(methodOn(ProfissionalController.class).findAll()).withSelfRel().getHref(), "GET"));

        return dto;
    }

    @Transactional
    @CacheEvict(value = {"profissionaisFindAll", "profissionaisFindById"}, allEntries = true)
    public ProfissionalResponseDTO create(ProfissionalCreateDTO dto) {
        logger.info("Criando um novo profissional...");
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("O e-mail informado já está em uso.");
        }

        var entity = DataMapper.parseObject(dto, Profissional.class);
        entity.setDataCadastro(new Date());
        var savedEntity = profissionalRepository.save(entity);
        logger.info("Profissional ID " + savedEntity.getId() + " criado com sucesso.");

        // Retorna o DTO com os links HATEOAS
        return findById(savedEntity.getId());
    }

    @Transactional
    @CachePut(value = "profissionaisFindById", key = "#id")
    @CacheEvict(value = "profissionaisFindAll", allEntries = true)
    public ProfissionalResponseDTO update(Long id, ProfissionalCreateDTO dto) {
        logger.info("Atualizando profissional com ID: " + id);
        var entity = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com o ID: " + id));

        if (!entity.getEmail().equals(dto.getEmail())) {
            usuarioRepository.findByEmail(dto.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(entity.getId())) {
                    throw new BusinessException("O novo e-mail informado já está em uso por outro usuário.");
                }
            });
            entity.setEmail(dto.getEmail());
        }

        entity.setNome(dto.getNome());
        entity.setHabilidades(dto.getHabilidades());
        entity.setNivelExperiencia(dto.getNivelExperiencia());
        entity.setCurriculoUrl(dto.getCurriculoUrl());

        var updatedEntity = profissionalRepository.save(entity);
        logger.info("Profissional ID " + updatedEntity.getId() + " atualizado com sucesso.");

        // Retorna o DTO com os links HATEOAS
        return findById(updatedEntity.getId());
    }

    @Transactional
    @CacheEvict(value = {"profissionaisFindAll", "profissionaisFindById"}, allEntries = true)
    public void delete(Long id) {
        logger.info("Tentando deletar profissional com ID: " + id);
        var entity = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com o ID: " + id));

        long activeTeamMemberships = membroEquipeRepository.findAll().stream()
                .filter(membro -> membro.getProfissional().getId().equals(id))
                .count();

        if (activeTeamMemberships > 0) {
            throw new BusinessException("Não é possível excluir o profissional pois ele é membro de " +
                    activeTeamMemberships + " equipe(s) de projeto(s).");
        }

        long activeInterests = interesseProjetoRepository.findByProfissionalId(id).stream()
                .filter(interesse -> interesse.getStatusInteresse() == StatusInteresse.PENDENTE ||
                        interesse.getStatusInteresse() == StatusInteresse.SELECIONADO ||
                        interesse.getStatusInteresse() == StatusInteresse.ALOCADO)
                .count();

        if (activeInterests > 0) {
            throw new BusinessException("Não é possível excluir o profissional pois ele possui " +
                    activeInterests + " interesse(s) ativo(s) em projetos.");
        }

        profissionalRepository.delete(entity);
        logger.info("Profissional com ID: " + id + " deletado com sucesso.");
    }
}