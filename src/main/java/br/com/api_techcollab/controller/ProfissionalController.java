package br.com.api_techcollab.controller;

import br.com.api_techcollab.dto.ProfissionalCreateDTO;
import br.com.api_techcollab.dto.ProfissionalResponseDTO;
import br.com.api_techcollab.services.ProfissionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profissionais")
public class ProfissionalController {

    @Autowired
    private ProfissionalService profissionalService;

    @GetMapping
    public ResponseEntity<List<ProfissionalResponseDTO>> findAll() {
        List<ProfissionalResponseDTO> profissionais = profissionalService.findAll();
        return ResponseEntity.ok(profissionais);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalResponseDTO> findById(@PathVariable Long id) {
        ProfissionalResponseDTO profissional = profissionalService.findById(id);
        return ResponseEntity.ok(profissional);
    }

    @PostMapping
    public ResponseEntity<ProfissionalResponseDTO> create(@RequestBody ProfissionalCreateDTO dto) {
        ProfissionalResponseDTO createdProfissional = profissionalService.create(dto);
        return new ResponseEntity<>(createdProfissional, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfissionalResponseDTO> update(@PathVariable Long id, @RequestBody ProfissionalCreateDTO dto) {
        ProfissionalResponseDTO updatedProfissional = profissionalService.update(id, dto);
        return ResponseEntity.ok(updatedProfissional);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        profissionalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}