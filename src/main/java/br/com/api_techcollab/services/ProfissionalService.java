package br.com.api_techcollab.service;

import br.com.api_techcollab.dto.ProfissionalCreateDTO;
import br.com.api_techcollab.dto.ProfissionalResponseDTO;
import br.com.api_techcollab.exceptions.ResourceNotFoundException;
import br.com.api_techcollab.mapper.DataMapper;
import br.com.api_techcollab.model.Profissional;
import br.com.api_techcollab.repository.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ProfissionalService {

    private final Logger logger = Logger.getLogger(ProfissionalService.class.getName());

    @Autowired
    private ProfissionalRepository profissionalRepository;

    /**
     * Retorna uma lista de todos os profissionais.
     * @return Lista de ProfissionalResponseDTO.
     */
    public List<ProfissionalResponseDTO> findAll() {
        logger.info("Buscando todos os profissionais...");
        var profissionais = profissionalRepository.findAll();
        return DataMapper.parseListObjects(profissionais, ProfissionalResponseDTO.class);
    }

    /**
     * Busca um profissional pelo ID.
     * @param id O ID do profissional.
     * @return O ProfissionalResponseDTO correspondente.
     * @throws ResourceNotFoundException se o profissional não for encontrado.
     */
    public ProfissionalResponseDTO findById(Long id) {
        logger.info("Buscando profissional pelo ID: " + id);
        var entity = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com o ID: " + id));
        return DataMapper.parseObject(entity, ProfissionalResponseDTO.class);
    }

    /**
     * Cria um novo profissional.
     * @param dto O DTO com os dados de criação.
     * @return O ProfissionalResponseDTO do profissional criado.
     */
    public ProfissionalResponseDTO create(ProfissionalCreateDTO dto) {
        logger.info("Criando um novo profissional...");
        var entity = DataMapper.parseObject(dto, Profissional.class);
        entity.setDataCadastro(new Date()); // Define a data de cadastro
        // O tipo de usuário é definido automaticamente no construtor da entidade Profissional
        var savedEntity = profissionalRepository.save(entity);
        return DataMapper.parseObject(savedEntity, ProfissionalResponseDTO.class);
    }

    /**
     * Atualiza os dados de um profissional existente.
     * @param id O ID do profissional a ser atualizado.
     * @param dto O DTO com os novos dados.
     * @return O ProfissionalResponseDTO do profissional atualizado.
     * @throws ResourceNotFoundException se o profissional não for encontrado.
     */
    public ProfissionalResponseDTO update(Long id, ProfissionalCreateDTO dto) {
        logger.info("Atualizando profissional com ID: " + id);
        var entity = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com o ID: " + id));

        entity.setNome(dto.getNome());
        entity.setEmail(dto.getEmail());
        entity.setHabilidades(dto.getHabilidades());
        entity.setNivelExperiencia(dto.getNivelExperiencia());
        entity.setCurriculoUrl(dto.getCurriculoUrl());
        // A senha pode ser atualizada em um método específico por segurança

        var updatedEntity = profissionalRepository.save(entity);
        return DataMapper.parseObject(updatedEntity, ProfissionalResponseDTO.class);
    }

    /**
     * Deleta um profissional pelo ID.
     * @param id O ID do profissional a ser deletado.
     */
    public void delete(Long id) {
        logger.info("Deletando profissional com ID: " + id);
        var entity = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com o ID: " + id));
        profissionalRepository.delete(entity);
    }
}