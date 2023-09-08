package com.jvolima.rinhabackend.repositories;

import com.jvolima.rinhabackend.entities.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    Pessoa findByApelido(String apelido);
}
