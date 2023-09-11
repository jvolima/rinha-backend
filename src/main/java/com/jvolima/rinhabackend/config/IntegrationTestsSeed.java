package com.jvolima.rinhabackend.config;

import com.jvolima.rinhabackend.entities.Pessoa;
import com.jvolima.rinhabackend.repositories.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.UUID;

@Profile("test")
@Configuration
public class IntegrationTestsSeed implements CommandLineRunner {

    @Autowired
    private PessoaRepository pessoaRepository;

    public static UUID pessoaId;

    @Override
    public void run(String... args) throws Exception {
        Pessoa pessoa1 = new Pessoa();
        pessoa1.setNome("John Doe");
        pessoa1.setApelido("johndoe");
        pessoa1.setNascimento("2001-08-10");
        pessoa1.getStack().add("Node");
        pessoa1.getStack().add("React");
        pessoa1 = pessoaRepository.save(pessoa1);
        pessoaId = pessoa1.getId();

        Pessoa pessoa2 = new Pessoa();
        pessoa2.setNome("Alice Smith");
        pessoa2.setApelido("alice");
        pessoa2.setNascimento("2002-07-04");
        pessoa2.getStack().add("Spring Boot");
        pessoa2.getStack().add("Go");
        pessoaRepository.save(pessoa2);
    }
}
