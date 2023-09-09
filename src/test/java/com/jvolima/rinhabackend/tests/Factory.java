package com.jvolima.rinhabackend.tests;

import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.entities.Pessoa;

public class Factory {

    public static Pessoa createPessoa() {
        Pessoa pessoa = new Pessoa();
        pessoa.setApelido("fulano123");
        pessoa.setNome("fulano da silva");
        pessoa.setNascimento("2000-10-10");
        pessoa.getStack().add("React");
        pessoa.getStack().add("Spring Boot");

        return pessoa;
    }

    public static PessoaDTO createPessoaDTO() {
        return new PessoaDTO(createPessoa());
    }
}
