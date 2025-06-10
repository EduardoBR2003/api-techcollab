package br.com.api_techcollab.services;

import br.com.api_techcollab.dto.ProjetoCreateDTO;
import br.com.api_techcollab.dto.ProjetoResponseDTO;
import br.com.api_techcollab.dto.ProjetoUpdateDTO;
import br.com.api_techcollab.dto.VagaProjetoCreateDTO; // Supondo que ProjetoCreateDTO possa ter vagas
import br.com.api_techcollab.exceptions.AccessDeniedException; // Criar esta exceção personalizada
import br.com.api_techcollab.exceptions.BusinessException; // Criar esta exceção personalizada
import br.com.api_techcollab.exceptions.ResourceNotFoundException;
import br.com.api_techcollab.mapper.DataMapper;
import br.com.api_techcollab.model.Empresa;
import br.com.api_techcollab.model.Projeto;
import br.com.api_techcollab.model.enums.StatusProjeto;
import br.com.api_techcollab.repository.EmpresaRepository;
import br.com.api_techcollab.repository.ProjetoRepository;
import br.com.api_techcollab.repository.VagaProjetoRepository; // Para limpar vagas ao excluir projeto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class ProjetoService {

    private final Logger logger = Logger.getLogger(ProjetoService.class.getName());

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private VagaProjetoService vagaProjetoService; // Para criar vagas

    @Autowired
    private VagaProjetoRepository vagaProjetoRepository; // Para limpar vagas

    @Transactional
    public ProjetoResponseDTO criarProjeto(ProjetoCreateDTO dto, Long empresaId) {
        logger.info("Criando novo projeto para empresa ID: " + empresaId);
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com ID: " + empresaId));

        Projeto projeto = DataMapper.parseObject(dto, Projeto.class);
        projeto.setEmpresa(empresa);
        projeto.setStatusProjeto(StatusProjeto.ABERTO_PARA_INTERESSE); // Status inicial
        // Data de criação do projeto pode ser definida aqui se necessário, ou via @PrePersist na entidade

        Projeto savedProjeto = projetoRepository.save(projeto);

        // Opcional: Criar vagas se vierem no DTO (ProjetoCreateDTO precisaria de List<VagaProjetoCreateDTO>)
        // if (dto.getVagas() != null && !dto.getVagas().isEmpty()) {
        //     for (VagaProjetoCreateDTO vagaDto : dto.getVagas()) {
        //         vagaProjetoService.criarVagaParaProjetoInterno(savedProjeto.getId(), vagaDto, empresaId);
        //     }
        // }
        logger.info("Projeto ID " + savedProjeto.getId() + " criado com sucesso.");
        return DataMapper.parseObject(savedProjeto, ProjetoResponseDTO.class);
    }

    @Transactional
    public ProjetoResponseDTO editarProjeto(Long projetoId, ProjetoUpdateDTO dto, Long empresaIdAutenticada) {
        logger.info("Editando projeto ID: " + projetoId + " pela empresa ID: " + empresaIdAutenticada);
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com ID: " + projetoId));

        if (!projeto.getEmpresa().getId().equals(empresaIdAutenticada)) {
            throw new AccessDeniedException("Empresa não autorizada a editar este projeto.");
        }

        // Atualizar campos permitidos
        if (dto.getTitulo() != null) projeto.setTitulo(dto.getTitulo());
        if (dto.getDescDetalhada() != null) projeto.setDescDetalhada(dto.getDescDetalhada());
        if (dto.getPrecoOfertado() != null) projeto.setPrecoOfertado(dto.getPrecoOfertado());
        if (dto.getDataInicioPrevista() != null) projeto.setDataInicioPrevista(dto.getDataInicioPrevista());
        if (dto.getDataConclusaoPrevista() != null) projeto.setDataConclusaoPrevista(dto.getDataConclusaoPrevista());
        if (dto.getStatusProjeto() != null) {
            // Adicionar regras para transição de status se necessário
            projeto.setStatusProjeto(dto.getStatusProjeto());
        }

        Projeto updatedProjeto = projetoRepository.save(projeto);
        logger.info("Projeto ID " + updatedProjeto.getId() + " atualizado com sucesso.");
        return DataMapper.parseObject(updatedProjeto, ProjetoResponseDTO.class);
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

    public List<ProjetoResponseDTO> consultarProjetosDisponiveis(/* filtros */) {
        logger.info("Consultando projetos disponíveis");
        // Implementar lógica de filtros (tecnologia, tipo, duração) aqui
        List<Projeto> projetos = projetoRepository.findAll().stream()
                .filter(p -> p.getStatusProjeto() == StatusProjeto.ABERTO_PARA_INTERESSE)
                .collect(Collectors.toList());
        return DataMapper.parseListObjects(projetos, ProjetoResponseDTO.class);
    }

    public List<ProjetoResponseDTO> consultarProjetosPorEmpresa(Long empresaId) {
        logger.info("Consultando projetos da empresa ID: " + empresaId);
        // Validação se a empresa existe pode ser adicionada
        List<Projeto> projetos = projetoRepository.findByEmpresaId(empresaId);
        return DataMapper.parseListObjects(projetos, ProjetoResponseDTO.class);
    }

    public ProjetoResponseDTO buscarProjetoPorId(Long projetoId) {
        logger.info("Buscando projeto por ID: " + projetoId);
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com ID: " + projetoId));
        return DataMapper.parseObject(projeto, ProjetoResponseDTO.class);
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
}