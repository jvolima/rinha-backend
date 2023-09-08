package com.jvolima.rinhabackend.services;

import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.entities.Pessoa;
import com.jvolima.rinhabackend.repositories.PessoaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class PessoaServiceTests {

    @InjectMocks
    private PessoaService pessoaService;

    @Mock
    private PessoaRepository pessoaRepository;

    private Pessoa pessoa;

    @BeforeEach
    public void setUp() throws Exception {
        pessoa = new Pessoa();
        pessoa.setApelido("apelido");
        pessoa.setNome("nome");
        pessoa.setNascimento("2004-11-10");
        pessoa.getStack().add("React");
        pessoa.getStack().add("Spring Boot");

        Mockito.when(pessoaRepository.save(ArgumentMatchers.any(Pessoa.class))).thenReturn(pessoa);
    }

    @Test
    public void insertShouldReturnPessoaDtoWhenDataIsValid() {
        PessoaDTO dto = pessoaService.insert(new PessoaDTO(pessoa));

        Assertions.assertNotNull(dto);
    }
}
