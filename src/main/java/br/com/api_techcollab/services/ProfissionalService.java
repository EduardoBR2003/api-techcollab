package br.com.api_techcollab.services;

import br.com.api_techcollab.dto.ProfissionalCreateDTO;
import br.com.api_techcollab.dto.ProfissionalResponseDTO;
import br.com.api_techcollab.exceptions.BusinessException; // Certifique-se que esta exceção existe
import br.com.api_techcollab.exceptions.ResourceNotFoundException;
import br.com.api_techcollab.mapper.DataMapper;
import br.com.api_techcollab.model.Profissional;
import br.com.api_techcollab.model.enums.StatusInteresse;
import br.com.api_techcollab.repository.InteresseProjetoRepository; // Para verificar interesses
import br.com.api_techcollab.repository.MembroEquipeRepository;   // Para verificar se é membro de equipe
import br.com.api_techcollab.repository.ProfissionalRepository;
import br.com.api_techcollab.repository.UsuarioRepository;       // Para verificar email único
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ProfissionalService {

    private final Logger logger = Logger.getLogger(ProfissionalService.class.getName());

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository; // Para checar e-mail

    @Autowired
    private MembroEquipeRepository membroEquipeRepository; // Para checar se é membro de equipe

    @Autowired
    private InteresseProjetoRepository interesseProjetoRepository; // Para checar interesses

    public List<ProfissionalResponseDTO> findAll() {
        logger.info("Buscando todos os profissionais...");
        var profissionais = profissionalRepository.findAll();
        return DataMapper.parseListObjects(profissionais, ProfissionalResponseDTO.class);
    }

    public ProfissionalResponseDTO findById(Long id) {
        logger.info("Buscando profissional pelo ID: " + id);
        var entity = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com o ID: " + id));
        return DataMapper.parseObject(entity, ProfissionalResponseDTO.class);
    }

    @Transactional
    public ProfissionalResponseDTO create(ProfissionalCreateDTO dto) {
        logger.info("Criando um novo profissional...");

        // Validar email único
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("O e-mail informado já está em uso.");
        }

        var entity = DataMapper.parseObject(dto, Profissional.class);
        entity.setDataCadastro(new Date());
        var savedEntity = profissionalRepository.save(entity);
        logger.info("Profissional ID " + savedEntity.getId() + " criado com sucesso.");
        return DataMapper.parseObject(savedEntity, ProfissionalResponseDTO.class);
    }

    @Transactional
    public ProfissionalResponseDTO update(Long id, ProfissionalCreateDTO dto) {
        logger.info("Atualizando profissional com ID: " + id);
        var entity = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com o ID: " + id));

        // Validar e-mail se alterado
        if (!entity.getEmail().equals(dto.getEmail())) {
            usuarioRepository.findByEmail(dto.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(entity.getId())) { // Garante que não é o e-mail do próprio usuário
                    throw new BusinessException("O novo e-mail informado já está em uso por outro usuário.");
                }
            });
            entity.setEmail(dto.getEmail());
        }

        entity.setNome(dto.getNome());
        entity.setHabilidades(dto.getHabilidades());
        entity.setNivelExperiencia(dto.getNivelExperiencia());
        entity.setCurriculoUrl(dto.getCurriculoUrl());
        // A senha pode ser atualizada em um método específico por segurança

        var updatedEntity = profissionalRepository.save(entity);
        logger.info("Profissional ID " + updatedEntity.getId() + " atualizado com sucesso.");
        return DataMapper.parseObject(updatedEntity, ProfissionalResponseDTO.class);
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Tentando deletar profissional com ID: " + id);
        var entity = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com o ID: " + id));

        // Regra de Negócio: Verificar se o profissional está ativo em equipes ou tem interesses importantes.
        long activeTeamMemberships = membroEquipeRepository.findAll().stream() // Melhorar consulta se possível
                .filter(membro -> membro.getProfissional().getId().equals(id))
                // Adicionar lógica para verificar se a equipe/projeto ainda está ativo, se necessário
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

        // Alternativa: Implementar exclusão lógica (marcar como inativo) em vez de exclusão física.
        // entity.setAtivo(false); profissionalRepository.save(entity);

        profissionalRepository.delete(entity);
        logger.info("Profissional com ID: " + id + " deletado com sucesso.");
    }
}