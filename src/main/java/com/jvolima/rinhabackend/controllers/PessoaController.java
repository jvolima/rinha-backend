package com.jvolima.rinhabackend.controllers;

import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.services.PessoaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = "/pessoas")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @PostMapping
    public ResponseEntity<Void> insert(@Valid @RequestBody PessoaDTO dto) {
        dto = pessoaService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PessoaDTO> findById(@PathVariable UUID id) {
        PessoaDTO dto = pessoaService.findById(id);

        return ResponseEntity.ok().body(dto);
    }
}
