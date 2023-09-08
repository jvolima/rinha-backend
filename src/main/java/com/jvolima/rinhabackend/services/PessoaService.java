package com.jvolima.rinhabackend.services;

import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.entities.Pessoa;
import com.jvolima.rinhabackend.repositories.PessoaRepository;
import com.jvolima.rinhabackend.services.exceptions.InvalidFieldException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public PessoaDTO insert(PessoaDTO dto) {
        if (dto.getApelido() == null) {
            throw new InvalidFieldException("Apelido: campo obrigatório");
        }

        if (dto.getApelido().length() > 32) {
            throw new InvalidFieldException("Apelido: máximo de 32 caracteres");
        }

        if (dto.getNome() == null) {
            throw new InvalidFieldException("Nome: campo obrigatório");
        }

        if (dto.getNome().length() > 100) {
            throw new InvalidFieldException("Nome: máximo de 100 caracteres");
        }

        if (dto.getNascimento() == null) {
            throw new InvalidFieldException("Nascimento: campo obrigatório");
        }

        Pessoa entity = new Pessoa();
        entity.setApelido(dto.getApelido());
        entity.setNome(dto.getNome());
        entity.setNascimento(dto.getNascimento());
        for (String stack : dto.getStack()) {
            entity.getStack().add(stack);
        }

        entity = pessoaRepository.save(entity);

        return new PessoaDTO(entity);
    }
}
