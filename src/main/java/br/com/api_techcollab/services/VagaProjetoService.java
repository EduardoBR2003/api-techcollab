package br.com.api_techcollab.services;

import br.com.api_techcollab.controller.ProjetoController;
import br.com.api_techcollab.controller.VagaProjetoController;
import br.com.api_techcollab.dto.CustomLink;
import br.com.api_techcollab.dto.VagaProjetoCreateDTO;
import br.com.api_techcollab.dto.VagaProjetoResponseDTO;
import br.com.api_techcollab.exceptions.AccessDeniedException;
import br.com.api_techcollab.exceptions.BusinessException;
import br.com.api_techcollab.exceptions.ResourceNotFoundException;
import br.com.api_techcollab.mapper.DataMapper;
import br.com.api_techcollab.model.Projeto;
import br.com.api_techcollab.model.VagaProjeto;
import br.com.api_techcollab.model.enums.StatusProjeto;
import br.com.api_techcollab.repository.InteresseProjetoRepository;
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
public class VagaProjetoService {

    private final Logger logger = Logger.getLogger(VagaProjetoService.class.getName());

    @Autowired
    private VagaProjetoRepository vagaProjetoRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private InteresseProjetoRepository interesseProjetoRepository;

    // MODIFICAÇÃO: Método helper para adicionar links HATEOAS CORRIGIDO
    private VagaProjetoResponseDTO adicionarLinksHATEOAS(VagaProjeto vaga) {
        VagaProjetoResponseDTO dto = DataMapper.parseObject(vaga, VagaProjetoResponseDTO.class);

        Long projetoId = vaga.getProjeto().getId();
        Long vagaId = vaga.getId();

        // Usando methodOn para construir links de forma segura, passando todos os PathVariables
        dto.getLinks().add(new CustomLink("self", linkTo(methodOn(VagaProjetoController.class).getVaga(projetoId, vagaId)).withSelfRel().getHref(), "GET"));
        dto.getLinks().add(new CustomLink("update", linkTo(methodOn(VagaProjetoController.class).editarVaga(projetoId, vagaId, null)).withSelfRel().getHref(), "PUT"));
        dto.getLinks().add(new CustomLink("delete", linkTo(methodOn(VagaProjetoController.class).excluirVaga(projetoId, vagaId, null)).withSelfRel().getHref(), "DELETE"));
        dto.getLinks().add(new CustomLink("projeto", linkTo(methodOn(ProjetoController.class).buscarProjetoPorId(projetoId)).withSelfRel().getHref(), "GET"));

        return dto;
    }

    public List<VagaProjetoResponseDTO> listarVagasPorProjeto(Long projetoId) {
        logger.info("Listando vagas do projeto ID: " + projetoId);
        if (!projetoRepository.existsById(projetoId)) {
            throw new ResourceNotFoundException("Projeto não encontrado com ID: " + projetoId);
        }
        List<VagaProjeto> vagas = vagaProjetoRepository.findByProjetoId(projetoId);

        return vagas.stream()
                .map(this::adicionarLinksHATEOAS)
                .collect(Collectors.toList());
    }

    // MODIFICAÇÃO: Novo método para buscar uma única vaga e adicionar links
    public VagaProjetoResponseDTO buscarVagaPorId(Long vagaId) {
        VagaProjeto vaga = vagaProjetoRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada com ID: " + vagaId));
        return adicionarLinksHATEOAS(vaga);
    }

    @Transactional
    public VagaProjetoResponseDTO criarVagaParaProjeto(Long projetoId, VagaProjetoCreateDTO vagaCreateDTO) {
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com ID: " + projetoId));

        if (projeto.getStatusProjeto() != StatusProjeto.ABERTO_PARA_INTERESSE && projeto.getStatusProjeto() != StatusProjeto.EM_FORMACAO_EQUIPE) {
            throw new BusinessException("Não é possível adicionar vaga ao projeto no status atual: " + projeto.getStatusProjeto());
        }

        VagaProjeto vaga = DataMapper.parseObject(vagaCreateDTO, VagaProjeto.class);
        vaga.setProjeto(projeto);

        VagaProjeto savedVaga = vagaProjetoRepository.save(vaga);
        logger.info("Vaga ID " + savedVaga.getId() + " criada com sucesso para o projeto ID " + projetoId +" da empresa "+ projeto.getTitulo());

        return adicionarLinksHATEOAS(savedVaga);
    }

    @Transactional
    public VagaProjetoResponseDTO editarVagaProjeto(Long vagaId, VagaProjetoCreateDTO vagaCreateDTO) {
        VagaProjeto vaga = vagaProjetoRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada com ID: " + vagaId));

        boolean hasInteressesAtivos = interesseProjetoRepository.findByVagaProjetoId(vagaId)
                .stream().anyMatch(i -> i.getStatusInteresse() == br.com.api_techcollab.model.enums.StatusInteresse.ALOCADO ||
                        i.getStatusInteresse() == br.com.api_techcollab.model.enums.StatusInteresse.SELECIONADO);

        if (hasInteressesAtivos) {
            throw new BusinessException("Vaga não pode ser editada pois possui interesses ativos ou profissionais alocados.");
        }
        if (vaga.getProjeto().getStatusProjeto() == StatusProjeto.DESENVOLVIMENTO || vaga.getProjeto().getStatusProjeto() == StatusProjeto.CONCLUIDO) {
            throw new BusinessException("Vaga não pode ser editada pois o projeto está em desenvolvimento ou concluído.");
        }

        vaga.setTituloVaga(vagaCreateDTO.getTituloVaga());
        vaga.setHabilidadesRequeridas(vagaCreateDTO.getHabilidadesRequeridas());
        vaga.setNivelExpDesejado(vagaCreateDTO.getNivelExpDesejado());
        vaga.setQuantFuncionarios(vagaCreateDTO.getQuantFuncionarios());

        VagaProjeto updatedVaga = vagaProjetoRepository.save(vaga);
        logger.info("Vaga ID " + updatedVaga.getId() + " atualizada com sucesso.");

        return adicionarLinksHATEOAS(updatedVaga);
    }

    @Transactional
    public void excluirVagaProjeto(Long vagaId, Long empresaIdAutenticada) {
        logger.info("Excluindo vaga ID: " + vagaId + " pela empresa ID: " + empresaIdAutenticada);
        VagaProjeto vaga = vagaProjetoRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada com ID: " + vagaId));

        if (!vaga.getProjeto().getEmpresa().getId().equals(empresaIdAutenticada)) {
            throw new AccessDeniedException("Empresa não autorizada a excluir esta vaga.");
        }

        long countInteressesAtivos = interesseProjetoRepository.findByVagaProjetoId(vagaId).stream()
                .filter(i -> i.getStatusInteresse() == br.com.api_techcollab.model.enums.StatusInteresse.PENDENTE ||
                        i.getStatusInteresse() == br.com.api_techcollab.model.enums.StatusInteresse.SELECIONADO ||
                        i.getStatusInteresse() == br.com.api_techcollab.model.enums.StatusInteresse.ALOCADO)
                .count();

        if (countInteressesAtivos > 0) {
            throw new BusinessException("Vaga não pode ser excluída pois possui " + countInteressesAtivos + " interesse(s) ativo(s). Cancele os interesses primeiro.");
        }

        vagaProjetoRepository.delete(vaga);
        logger.info("Vaga ID " + vagaId + " excluída com sucesso.");
    }

    public VagaProjeto buscarVagaPorIdEntidade(Long vagaId) {
        return vagaProjetoRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada com ID: " + vagaId));
    }
}