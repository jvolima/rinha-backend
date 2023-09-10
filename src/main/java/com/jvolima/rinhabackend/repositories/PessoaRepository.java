package com.jvolima.rinhabackend.repositories;

import com.jvolima.rinhabackend.entities.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {

    @Query("SELECT DISTINCT p FROM Pessoa p " +
            "LEFT JOIN p.stack s " +
            "WHERE p.apelido LIKE %:termo% " +
            "OR p.nome LIKE %:termo% " +
            "OR p.nascimento LIKE %:termo% " +
            "OR s LIKE %:termo%")
    List<Pessoa> findAllBySubstring(String searchTerm);

    Pessoa findByApelido(String apelido);
}
