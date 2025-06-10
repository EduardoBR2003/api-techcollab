package br.com.api_techcollab.services;

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

@Service
public class VagaProjetoService {

    private final Logger logger = Logger.getLogger(VagaProjetoService.class.getName());

    @Autowired
    private VagaProjetoRepository vagaProjetoRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private InteresseProjetoRepository interesseProjetoRepository; // Para verificar interesses ao excluir vaga

    @Transactional
    public VagaProjetoResponseDTO criarVagaParaProjeto(Long projetoId, VagaProjetoCreateDTO dto) {
        logger.info("Criando vaga para projeto ID: " + projetoId );
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com ID: " + projetoId));


        if (projeto.getStatusProjeto() != StatusProjeto.ABERTO_PARA_INTERESSE && projeto.getStatusProjeto() != StatusProjeto.EM_FORMACAO_EQUIPE) {
            throw new BusinessException("Não é possível adicionar vaga ao projeto no status atual: " + projeto.getStatusProjeto());
        }

        VagaProjeto vaga = DataMapper.parseObject(dto, VagaProjeto.class);
        vaga.setProjeto(projeto);

        VagaProjeto savedVaga = vagaProjetoRepository.save(vaga);
        logger.info("Vaga ID " + savedVaga.getId() + " criada com sucesso para o projeto ID " + projetoId +" da empresa "+ projeto.getTitulo());
        return DataMapper.parseObject(savedVaga, VagaProjetoResponseDTO.class);
    }

    public List<VagaProjetoResponseDTO> listarVagasPorProjeto(Long projetoId) {
        logger.info("Listando vagas do projeto ID: " + projetoId);
        if (!projetoRepository.existsById(projetoId)) {
            throw new ResourceNotFoundException("Projeto não encontrado com ID: " + projetoId);
        }
        List<VagaProjeto> vagas = vagaProjetoRepository.findByProjetoId(projetoId);
        return DataMapper.parseListObjects(vagas, VagaProjetoResponseDTO.class);
    }

    @Transactional
    public VagaProjetoResponseDTO editarVagaProjeto(Long vagaId, VagaProjetoCreateDTO dto) {
        logger.info("Editando vaga ID: " + vagaId);
        VagaProjeto vaga = vagaProjetoRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada com ID: " + vagaId));

        // Regra: não editar se houver profissionais alocados ou projeto em status avançado
        boolean hasInteressesAtivos = interesseProjetoRepository.findByVagaProjetoId(vagaId)
                .stream().anyMatch(i -> i.getStatusInteresse() == br.com.api_techcollab.model.enums.StatusInteresse.ALOCADO ||
                        i.getStatusInteresse() == br.com.api_techcollab.model.enums.StatusInteresse.SELECIONADO);

        if (hasInteressesAtivos) {
            throw new BusinessException("Vaga não pode ser editada pois possui interesses ativos ou profissionais alocados.");
        }
        if (vaga.getProjeto().getStatusProjeto() == StatusProjeto.DESENVOLVIMENTO || vaga.getProjeto().getStatusProjeto() == StatusProjeto.CONCLUIDO) {
            throw new BusinessException("Vaga não pode ser editada pois o projeto está em desenvolvimento ou concluído.");
        }


        vaga.setTituloVaga(dto.getTituloVaga());
        vaga.setHabilidadesRequeridas(dto.getHabilidadesRequeridas());
        vaga.setNivelExpDesejado(dto.getNivelExpDesejado());
        vaga.setQuantFuncionarios(dto.getQuantFuncionarios());

        VagaProjeto updatedVaga = vagaProjetoRepository.save(vaga);
        logger.info("Vaga ID " + updatedVaga.getId() + " atualizada com sucesso.");
        return DataMapper.parseObject(updatedVaga, VagaProjetoResponseDTO.class);
    }

    @Transactional
    public void excluirVagaProjeto(Long vagaId, Long empresaIdAutenticada) {
        logger.info("Excluindo vaga ID: " + vagaId + " pela empresa ID: " + empresaIdAutenticada);
        VagaProjeto vaga = vagaProjetoRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada com ID: " + vagaId));

        if (!vaga.getProjeto().getEmpresa().getId().equals(empresaIdAutenticada)) {
            throw new AccessDeniedException("Empresa não autorizada a excluir esta vaga.");
        }

        // Regra: Verificar se há interesses PENDENTES ou SELECIONADOS para esta vaga.
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