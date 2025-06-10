package br.com.api_techcollab.services;

import br.com.api_techcollab.dto.EmpresaCreateDTO;
import br.com.api_techcollab.dto.EmpresaResponseDTO;
import br.com.api_techcollab.exceptions.BusinessException; // Certifique-se que esta exceção existe
import br.com.api_techcollab.exceptions.ResourceNotFoundException;
import br.com.api_techcollab.mapper.DataMapper;
import br.com.api_techcollab.model.Empresa;
import br.com.api_techcollab.repository.EmpresaRepository;
import br.com.api_techcollab.repository.ProjetoRepository; // Para verificar projetos
import br.com.api_techcollab.repository.UsuarioRepository; // Para verificar email único
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class EmpresaService {

    private final Logger logger = Logger.getLogger(EmpresaService.class.getName());

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository; // Para checar e-mail

    @Autowired
    private ProjetoRepository projetoRepository; // Para checar projetos antes de deletar

    public List<EmpresaResponseDTO> findAll() {
        logger.info("Buscando todas as empresas...");
        var empresas = empresaRepository.findAll();
        return DataMapper.parseListObjects(empresas, EmpresaResponseDTO.class);
    }

    public EmpresaResponseDTO findById(Long id) {
        logger.info("Buscando empresa pelo ID: " + id);
        var entity = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));
        return DataMapper.parseObject(entity, EmpresaResponseDTO.class);
    }

    @Transactional
    public EmpresaResponseDTO create(EmpresaCreateDTO dto) {
        logger.info("Criando uma nova empresa...");

        // Validar email único
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("O e-mail informado já está em uso.");
        }
        // Validar CNPJ único
        if (empresaRepository.findByCnpj(dto.getCnpj()).isPresent()) {
            throw new BusinessException("O CNPJ informado já está cadastrado.");
        }

        var entity = DataMapper.parseObject(dto, Empresa.class);
        entity.setDataCadastro(new Date());
        var savedEntity = empresaRepository.save(entity);
        logger.info("Empresa ID " + savedEntity.getId() + " criada com sucesso.");
        return DataMapper.parseObject(savedEntity, EmpresaResponseDTO.class);
    }

    @Transactional
    public EmpresaResponseDTO update(Long id, EmpresaCreateDTO dto) {
        logger.info("Atualizando empresa com ID: " + id);
        var entity = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));

        // Validar e-mail se alterado
        if (!entity.getEmail().equals(dto.getEmail())) {
            usuarioRepository.findByEmail(dto.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(entity.getId())) { // Garante que não é o e-mail do próprio usuário
                    throw new BusinessException("O novo e-mail informado já está em uso por outro usuário.");
                }
            });
            entity.setEmail(dto.getEmail());
        }

        // Validar CNPJ se alterado
        if (!entity.getCnpj().equals(dto.getCnpj())) {
            empresaRepository.findByCnpj(dto.getCnpj()).ifPresent(existingEmpresa -> {
                if (!existingEmpresa.getId().equals(entity.getId())) { // Garante que não é o CNPJ da própria empresa
                    throw new BusinessException("O novo CNPJ informado já está cadastrado para outra empresa.");
                }
            });
            entity.setCnpj(dto.getCnpj());
        }

        entity.setNome(dto.getNome());
        entity.setRazaoSocial(dto.getRazaoSocial());
        entity.setDescEmpresa(dto.getDescEmpresa());
        entity.setSiteUrl(dto.getSiteUrl());
        // A senha deve ser atualizada em um método específico por segurança

        var updatedEntity = empresaRepository.save(entity);
        logger.info("Empresa ID " + updatedEntity.getId() + " atualizada com sucesso.");
        return DataMapper.parseObject(updatedEntity, EmpresaResponseDTO.class);
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Tentando deletar empresa com ID: " + id);
        var entity = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com o ID: " + id));

        // Regra de Negócio: Verificar se a empresa possui projetos associados.
        // O BD (com FK RESTRICT) já impediria, mas uma verificação no service é mais amigável.
        long projectCount = projetoRepository.findByEmpresaId(id).size();
        if (projectCount > 0) {
            throw new BusinessException("Não é possível excluir a empresa pois ela possui " + projectCount + " projeto(s) associado(s). " +
                    "Considere arquivar os projetos ou transferi-los antes de excluir a empresa.");
        }

        empresaRepository.delete(entity);
        logger.info("Empresa com ID: " + id + " deletada com sucesso.");
    }
}