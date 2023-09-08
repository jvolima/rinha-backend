package com.jvolima.rinhabackend.tests;

import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.entities.Pessoa;

public class Factory {

    public static Pessoa createPessoa() {
        Pessoa pessoa = new Pessoa();
        pessoa.setApelido("apelido");
        pessoa.setNome("nome");
        pessoa.setNascimento("2004-11-10");
        pessoa.getStack().add("React");
        pessoa.getStack().add("Spring Boot");

        return pessoa;
    }

    public static PessoaDTO createPessoaDTO() {
        return new PessoaDTO(createPessoa());
    }
}
