package com.jvolima.rinhabackend.repositories;

import com.jvolima.rinhabackend.entities.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {
    Pessoa findByApelido(String apelido);
}
