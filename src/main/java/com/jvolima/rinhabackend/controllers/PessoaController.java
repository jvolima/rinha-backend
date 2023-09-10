package com.jvolima.rinhabackend.controllers;

import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.services.PessoaService;
import com.jvolima.rinhabackend.services.exceptions.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/pessoas")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

   @GetMapping
    public ResponseEntity<List<PessoaDTO>> findAllBySubstring(@RequestParam String t) {
        List<PessoaDTO> list = pessoaService.findAllBySubstring(t);

        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PessoaDTO> findById(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            PessoaDTO dto = pessoaService.findById(uuid);

            return ResponseEntity.ok().body(dto);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Id " + id + " not found");
        }
    }

    @PostMapping
    public ResponseEntity<Void> insert(@Valid @RequestBody PessoaDTO dto) {
        dto = pessoaService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }
}
