package br.com.api_techcollab.services;

import br.com.api_techcollab.controller.EmpresaController;
import br.com.api_techcollab.dto.CustomLink;
import br.com.api_techcollab.dto.EmpresaCreateDTO;
import br.com.api_techcollab.dto.EmpresaResponseDTO;
import br.com.api_techcollab.exceptions.BusinessException;
import br.com.api_techcollab.exceptions.ResourceNotFoundException;
import br.com.api_techcollab.mapper.DataMapper;
import br.com.api_techcollab.model.Empresa;
import br.com.api_techcollab.repository.EmpresaRepository;
import br.com.api_techcollab.repository.ProjetoRepository;
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
public class EmpresaService {

    private final Logger logger = Logger.getLogger(EmpresaService.class.getName());

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Cacheable("empresasFindAll")
    public List<EmpresaResponseDTO> findAll() {
        logger.info("Buscando todas as empresas...");
        var empresas = empresaRepository.findAll();
        var dtos = DataMapper.parseListObjects(empresas, EmpresaResponseDTO.class);

        dtos.forEach(dto -> {
            try {
                // MODIFICAÇÃO: Lógica HATEOAS Customizada para a lista
                dto.getLinks().add(new CustomLink("self", linkTo(methodOn(EmpresaController.class).findById(dto.getId())).withSelfRel().getHref(), "GET"));
            } catch (Exception e) {
                logger.warning("Erro ao gerar link HATEOAS para a empresa ID: " + dto.getId());
            }
        });

        return dtos;
    }

    @Cacheable(value = "empresasFindById", key = "#id")
    public EmpresaResponseDTO findById(Long id) {
        logger.info("Buscando empresa pelo ID: " + id);
        var entity = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));

        EmpresaResponseDTO dto = DataMapper.parseObject(entity, EmpresaResponseDTO.class);

        // MODIFICAÇÃO: Lógica HATEOAS Customizada para um único recurso
        dto.getLinks().add(new CustomLink("self", linkTo(methodOn(EmpresaController.class).findById(id)).withSelfRel().getHref(), "GET"));
        dto.getLinks().add(new CustomLink("update", linkTo(methodOn(EmpresaController.class).update(id, null)).withSelfRel().getHref(), "PUT"));
        dto.getLinks().add(new CustomLink("delete", linkTo(methodOn(EmpresaController.class).delete(id)).withSelfRel().getHref(), "DELETE"));
        dto.getLinks().add(new CustomLink("projetos", linkTo(methodOn(EmpresaController.class).consultarProjetosPorEmpresa(id)).withSelfRel().getHref(), "GET"));
        dto.getLinks().add(new CustomLink("todas_as_empresas", linkTo(methodOn(EmpresaController.class).findAll()).withSelfRel().getHref(), "GET"));

        return dto;
    }

    @Transactional
    @CacheEvict(value = {"empresasFindAll", "empresasFindById"}, allEntries = true)
    public EmpresaResponseDTO create(EmpresaCreateDTO dto) {
        logger.info("Criando uma nova empresa...");

        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("O e-mail informado já está em uso.");
        }
        if (empresaRepository.findByCnpj(dto.getCnpj()).isPresent()) {
            throw new BusinessException("O CNPJ informado já está cadastrado.");
        }

        var entity = DataMapper.parseObject(dto, Empresa.class);
        entity.setDataCadastro(new Date());
        var savedEntity = empresaRepository.save(entity);
        logger.info("Empresa ID " + savedEntity.getId() + " criada com sucesso.");

        // Retorna o DTO com os links HATEOAS
        return findById(savedEntity.getId());
    }

    @Transactional
    @CachePut(value = "empresasFindById", key = "#id")
    @CacheEvict(value = "empresasFindAll", allEntries = true)
    public EmpresaResponseDTO update(Long id, EmpresaCreateDTO dto) {
        logger.info("Atualizando empresa com ID: " + id);
        var entity = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));

        // ... (lógica de validação) ...

        entity.setNome(dto.getNome());
        entity.setRazaoSocial(dto.getRazaoSocial());
        entity.setDescEmpresa(dto.getDescEmpresa());
        entity.setSiteUrl(dto.getSiteUrl());

        var updatedEntity = empresaRepository.save(entity);
        logger.info("Empresa ID " + updatedEntity.getId() + " atualizada com sucesso.");

        // Retorna o DTO com os links HATEOAS
        return findById(updatedEntity.getId());
    }

    @Transactional
    @CacheEvict(value = {"empresasFindAll", "empresasFindById"}, allEntries = true)
    public void delete(Long id) {
        logger.info("Tentando deletar empresa com ID: " + id);
        var entity = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));

        long projectCount = projetoRepository.findByEmpresaId(id).size();
        if (projectCount > 0) {
            throw new BusinessException("Não é possível excluir a empresa pois ela possui " + projectCount + " projeto(s) associado(s). " +
                    "Considere arquivar os projetos ou transferi-los antes de excluir a empresa.");
        }

        empresaRepository.delete(entity);
        logger.info("Empresa com ID: " + id + " deletada com sucesso.");
    }
}