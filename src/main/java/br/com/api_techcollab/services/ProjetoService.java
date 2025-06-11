package br.com.api_techcollab.services;

import br.com.api_techcollab.controller.*;
import br.com.api_techcollab.dto.CustomLink;
import br.com.api_techcollab.dto.ProjetoCreateDTO;
import br.com.api_techcollab.dto.ProjetoResponseDTO;
import br.com.api_techcollab.dto.ProjetoUpdateDTO;
import br.com.api_techcollab.exceptions.AccessDeniedException;
import br.com.api_techcollab.exceptions.BusinessException;
import br.com.api_techcollab.exceptions.ResourceNotFoundException;
import br.com.api_techcollab.mapper.DataMapper;
import br.com.api_techcollab.model.Empresa;
import br.com.api_techcollab.model.Projeto;
import br.com.api_techcollab.model.enums.StatusProjeto;
import br.com.api_techcollab.repository.EmpresaRepository;
import br.com.api_techcollab.repository.ProjetoRepository;
import br.com.api_techcollab.repository.VagaProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ProjetoService {

    private final Logger logger = Logger.getLogger(ProjetoService.class.getName());

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private VagaProjetoRepository vagaProjetoRepository; // Para limpar vagas

    @Transactional
    public ProjetoResponseDTO criarProjeto(ProjetoCreateDTO dto, Long empresaId) {
        logger.info("Criando novo projeto para empresa ID: " + empresaId);
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com ID: " + empresaId));

        Projeto projeto = DataMapper.parseObject(dto, Projeto.class);
        projeto.setEmpresa(empresa);
        projeto.setStatusProjeto(StatusProjeto.ABERTO_PARA_INTERESSE);

        Projeto savedProjeto = projetoRepository.save(projeto);
        logger.info("Projeto ID " + savedProjeto.getId() + " criado com sucesso.");
        return buscarProjetoPorId(savedProjeto.getId()); // Retorna com links
    }

    @Transactional
    public ProjetoResponseDTO editarProjeto(Long projetoId, ProjetoUpdateDTO dto, Long empresaIdAutenticada) {
        logger.info("Editando projeto ID: " + projetoId + " pela empresa ID: " + empresaIdAutenticada);
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com ID: " + projetoId));

        if (!projeto.getEmpresa().getId().equals(empresaIdAutenticada)) {
            throw new AccessDeniedException("Empresa não autorizada a editar este projeto.");
        }

        if (dto.getTitulo() != null) projeto.setTitulo(dto.getTitulo());
        if (dto.getDescDetalhada() != null) projeto.setDescDetalhada(dto.getDescDetalhada());
        if (dto.getPrecoOfertado() != null) projeto.setPrecoOfertado(dto.getPrecoOfertado());
        if (dto.getDataInicioPrevista() != null) projeto.setDataInicioPrevista(dto.getDataInicioPrevista());
        if (dto.getDataConclusaoPrevista() != null) projeto.setDataConclusaoPrevista(dto.getDataConclusaoPrevista());
        if (dto.getStatusProjeto() != null) {
            projeto.setStatusProjeto(dto.getStatusProjeto());
        }

        Projeto updatedProjeto = projetoRepository.save(projeto);
        logger.info("Projeto ID " + updatedProjeto.getId() + " atualizado com sucesso.");
        return buscarProjetoPorId(updatedProjeto.getId()); // Retorna com links
    }

    @Transactional
    public void excluirProjeto(Long projetoId, Long empresaIdAutenticada) {
        logger.info("Excluindo projeto ID: " + projetoId + " pela empresa ID: " + empresaIdAutenticada);
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com ID: " + projetoId));

        if (!projeto.getEmpresa().getId().equals(empresaIdAutenticada)) {
            throw new AccessDeniedException("Empresa não autorizada a excluir este projeto.");
        }

        // Regra: Não permitir exclusão se o projeto não estiver em um estado inicial ou cancelado.
        if (!(projeto.getStatusProjeto() == StatusProjeto.ABERTO_PARA_INTERESSE /* || projeto.getStatusProjeto() == StatusProjeto.CANCELADO */)) {
            throw new BusinessException("Projeto não pode ser excluído pois está no status: " + projeto.getStatusProjeto());
        }

        // Regra: Tratar dependências (Interesses, Vagas, Equipes)
        // Exemplo simplificado: excluir vagas associadas. Interesses e equipes exigiriam mais lógica.
        var vagasDoProjeto = vagaProjetoRepository.findByProjetoId(projetoId);
        vagaProjetoRepository.deleteAll(vagasDoProjeto); // Cuidado com outras dependências das vagas

        // Excluir interesses associados às vagas (requer InteresseProjetoRepository)
        // Excluir equipe e membros (requer EquipeProjetoRepository e MembroEquipeRepository)

        projetoRepository.delete(projeto);
        logger.info("Projeto ID " + projetoId + " excluído com sucesso.");
    }

    public List<ProjetoResponseDTO> consultarProjetosDisponiveis() {
        logger.info("Consultando projetos disponíveis");
        List<Projeto> projetos = projetoRepository.findAll().stream()
                .filter(p -> p.getStatusProjeto() == StatusProjeto.ABERTO_PARA_INTERESSE)
                .collect(Collectors.toList());

        List<ProjetoResponseDTO> projetosDTO = DataMapper.parseListObjects(projetos, ProjetoResponseDTO.class);

        projetosDTO.forEach(p -> {
            try {
                // MODIFICAÇÃO: Lógica HATEOAS Customizada
                p.getLinks().add(new CustomLink("self", linkTo(methodOn(ProjetoController.class).buscarProjetoPorId(p.getId())).withSelfRel().getHref(), "GET"));
            } catch (Exception e) {
                logger.warning("Erro ao gerar link HATEOAS para o projeto ID: " + p.getId());
            }
        });

        return projetosDTO;
    }

    public List<ProjetoResponseDTO> consultarProjetosPorEmpresa(Long empresaId) {
        logger.info("Consultando projetos da empresa ID: " + empresaId);
        if (!empresaRepository.existsById(empresaId)) {
            throw new ResourceNotFoundException("Empresa não encontrada com ID: " + empresaId);
        }
        List<Projeto> projetos = projetoRepository.findByEmpresaId(empresaId);

        // Reutiliza o método buscarProjetoPorId para garantir que cada projeto na lista tenha seus links HATEOAS
        return projetos.stream()
                .map(projeto -> this.buscarProjetoPorId(projeto.getId()))
                .collect(Collectors.toList());
    }

    // Método para atualizar status do projeto, usado por outros services
    @Transactional
    public void atualizarStatusProjeto(Long projetoId, StatusProjeto novoStatus) {
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com ID: " + projetoId));
        projeto.setStatusProjeto(novoStatus);
        projetoRepository.save(projeto);
        logger.info("Status do Projeto ID " + projetoId + " atualizado para " + novoStatus);
    }

    public ProjetoResponseDTO buscarProjetoPorId(Long projetoId) {
        logger.info("Buscando projeto por ID: " + projetoId);
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com ID: " + projetoId));

        ProjetoResponseDTO projetoDTO = DataMapper.parseObject(projeto, ProjetoResponseDTO.class);

        projetoDTO.getLinks().add(new CustomLink("self", linkTo(methodOn(ProjetoController.class).buscarProjetoPorId(projetoId)).withSelfRel().getHref(), "GET"));
        projetoDTO.getLinks().add(new CustomLink("update", linkTo(methodOn(ProjetoController.class).editarProjeto(projetoId, null)).withSelfRel().getHref(), "PUT"));
        projetoDTO.getLinks().add(new CustomLink("delete", linkTo(methodOn(ProjetoController.class).excluirProjeto(projetoId, null)).withSelfRel().getHref(), "DELETE"));
        projetoDTO.getLinks().add(new CustomLink("empresa_proprietaria", linkTo(methodOn(EmpresaController.class).findById(projeto.getEmpresa().getId())).withSelfRel().getHref(), "GET"));
        projetoDTO.getLinks().add(new CustomLink("vagas", linkTo(methodOn(VagaProjetoController.class).listarVagasPorProjeto(projetoId)).withSelfRel().getHref(), "GET"));
        projetoDTO.getLinks().add(new CustomLink("equipe", linkTo(methodOn(EquipeProjetoController.class).visualizarEquipeProjeto(projetoId)).withSelfRel().getHref(), "GET"));
        projetoDTO.getLinks().add(new CustomLink("projetos_disponiveis", linkTo(methodOn(ProjetoController.class).consultarProjetosDisponiveis()).withSelfRel().getHref(), "GET"));

        return projetoDTO;
    }
}