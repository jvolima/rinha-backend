package com.jvolima.rinhabackend.services;

import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.entities.Pessoa;
import com.jvolima.rinhabackend.repositories.PessoaRepository;
import com.jvolima.rinhabackend.services.exceptions.BadRequestException;
import com.jvolima.rinhabackend.services.exceptions.UnprocessableEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Transactional(readOnly = true)
    public PessoaDTO findById(UUID id) {
        Optional<Pessoa> optionalEntity = pessoaRepository.findById(id);
        Pessoa entity = optionalEntity.get();

        return new PessoaDTO(entity);
    }

    @Transactional
    public PessoaDTO insert(PessoaDTO dto) {
        String stringWithOnlyNumbers = "^[0-9]+$";

        if (dto.getApelido().matches(stringWithOnlyNumbers)) {
            throw new BadRequestException("Apelido não pode ser do tipo número");
        }

        if (dto.getNome().matches(stringWithOnlyNumbers)) {
            throw new BadRequestException("Nome não pode ser do tipo número");
        }

        for (String stack: dto.getStack()) {
            if (stack.matches(stringWithOnlyNumbers)) {
                throw new BadRequestException("Nenhuma stack pode ser do tipo número");
            }
        }

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
