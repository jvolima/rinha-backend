package com.jvolima.rinhabackend.repositories;

import com.jvolima.rinhabackend.entities.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {

    @Query("SELECT DISTINCT p FROM Pessoa p LEFT JOIN p.stack s " +
            "WHERE p.apelido ILIKE %:searchTerm% OR p.nome ILIKE %:searchTerm% " +
            "OR p.nascimento ILIKE %:searchTerm% OR s ILIKE %:searchTerm%")
    List<Pessoa> findAllBySubstring(String searchTerm);

    Pessoa findByApelido(String apelido);
}
