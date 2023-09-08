package com.jvolima.rinhabackend.services;

import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.entities.Pessoa;
import com.jvolima.rinhabackend.repositories.PessoaRepository;
import com.jvolima.rinhabackend.services.exceptions.InvalidFieldException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        Pattern pattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        Matcher matcher = pattern.matcher(dto.getNascimento());

        if  (!matcher.matches()) {
            throw new InvalidFieldException("Nascimento: formato inválido, use o formato AAAA-MM-DD");
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
