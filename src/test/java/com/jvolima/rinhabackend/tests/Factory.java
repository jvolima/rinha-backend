package com.jvolima.rinhabackend.tests;

import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.entities.Pessoa;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

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

    public static List<Pessoa> createPessoaList() {
        Pessoa pessoa1 = createPessoa();

        Pessoa pessoa2 = createPessoa();
        pessoa2.setApelido("fulano321");
        pessoa2.getStack().clear();
        pessoa2.getStack().add("Php");
        pessoa2.getStack().add("Ruby on rails");

        return Arrays.asList(pessoa1, pessoa2);
    }

    public static PessoaDTO createPessoaDTO() {
        return new PessoaDTO(createPessoa());
    }
}
