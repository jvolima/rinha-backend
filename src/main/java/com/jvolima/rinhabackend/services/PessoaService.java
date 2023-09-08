package com.jvolima.rinhabackend.services;

import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.entities.Pessoa;
import com.jvolima.rinhabackend.repositories.PessoaRepository;
import com.jvolima.rinhabackend.services.exceptions.UnprocessableEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public PessoaDTO insert(PessoaDTO dto) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate parsedDate = LocalDate.parse(dto.getNascimento(), formatter);
            LocalDate parsedNowDate = LocalDate.now();

            if (parsedDate.isAfter(parsedNowDate)) {
                throw new UnprocessableEntityException("Data de nascimento inválida");
            }
        } catch (DateTimeParseException e) {
            throw new UnprocessableEntityException("Data de nascimento inválida");
        }

        Pessoa entityAlreadyExist = pessoaRepository.findByApelido(dto.getApelido());

        if (entityAlreadyExist != null) {
            throw new UnprocessableEntityException("Apelido já existe");
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
