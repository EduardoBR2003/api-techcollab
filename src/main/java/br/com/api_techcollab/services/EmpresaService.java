package br.com.api_techcollab.service;

import br.com.api_techcollab.dto.EmpresaCreateDTO;
import br.com.api_techcollab.dto.EmpresaResponseDTO;
import br.com.api_techcollab.exceptions.ResourceNotFoundException;
import br.com.api_techcollab.mapper.DataMapper;
import br.com.api_techcollab.model.Empresa;
import br.com.api_techcollab.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class EmpresaService {

    private final Logger logger = Logger.getLogger(EmpresaService.class.getName());

    @Autowired
    private EmpresaRepository empresaRepository;

    /**
     * Retorna uma lista de todas as empresas.
     * @return Lista de EmpresaResponseDTO.
     */
    public List<EmpresaResponseDTO> findAll() {
        logger.info("Buscando todas as empresas...");
        var empresas = empresaRepository.findAll();
        return DataMapper.parseListObjects(empresas, EmpresaResponseDTO.class);
    }

    /**
     * Busca uma empresa pelo ID.
     * @param id O ID da empresa.
     * @return O EmpresaResponseDTO correspondente.
     * @throws ResourceNotFoundException se a empresa não for encontrada.
     */
    public EmpresaResponseDTO findById(Long id) {
        logger.info("Buscando empresa pelo ID: " + id);
        var entity = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));
        return DataMapper.parseObject(entity, EmpresaResponseDTO.class);
    }

    /**
     * Cria uma nova empresa.
     * @param dto O DTO com os dados de criação.
     * @return O EmpresaResponseDTO da empresa criada.
     */
    public EmpresaResponseDTO create(EmpresaCreateDTO dto) {
        logger.info("Criando uma nova empresa...");
        var entity = DataMapper.parseObject(dto, Empresa.class);
        entity.setDataCadastro(new Date()); // Define a data de cadastro
        // O tipo de usuário é definido automaticamente no construtor da entidade Empresa
        var savedEntity = empresaRepository.save(entity);
        return DataMapper.parseObject(savedEntity, EmpresaResponseDTO.class);
    }

    /**
     * Atualiza os dados de uma empresa existente.
     * @param id O ID da empresa a ser atualizada.
     * @param dto O DTO com os novos dados.
     * @return O EmpresaResponseDTO da empresa atualizada.
     * @throws ResourceNotFoundException se a empresa não for encontrada.
     */
    public EmpresaResponseDTO update(Long id, EmpresaCreateDTO dto) {
        logger.info("Atualizando empresa com ID: " + id);
        var entity = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));

        entity.setNome(dto.getNome());
        entity.setEmail(dto.getEmail());
        entity.setCnpj(dto.getCnpj());
        entity.setRazaoSocial(dto.getRazaoSocial());
        entity.setDescEmpresa(dto.getDescEmpresa());
        entity.setSiteUrl(dto.getSiteUrl());

        var updatedEntity = empresaRepository.save(entity);
        return DataMapper.parseObject(updatedEntity, EmpresaResponseDTO.class);
    }

    /**
     * Deleta uma empresa pelo ID.
     * @param id O ID da empresa a ser deletada.
     */
    public void delete(Long id) {
        logger.info("Deletando empresa com ID: " + id);
        var entity = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));
        empresaRepository.delete(entity);
    }
}