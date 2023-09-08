package com.jvolima.rinhabackend.services;

import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.entities.Pessoa;
import com.jvolima.rinhabackend.repositories.PessoaRepository;
import com.jvolima.rinhabackend.services.exceptions.InvalidFieldException;
import com.jvolima.rinhabackend.tests.Factory;
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

    @BeforeEach
    public void setUp() {
        Mockito.when(pessoaRepository.save(ArgumentMatchers.any(Pessoa.class))).thenReturn(Factory.createPessoa());
    }

    @Test
    public void insertShouldReturnPessoaDtoWhenDataIsValid() {
        PessoaDTO dto = pessoaService.insert(Factory.createPessoaDTO());

        Assertions.assertNotNull(dto);
    }

    @Test
    public void insertShouldThrowInvalidFieldExceptionWhenApelidoIsNull() {
        PessoaDTO dto = pessoaService.insert(Factory.createPessoaDTO());
        dto.setApelido(null);

        Assertions.assertThrows(InvalidFieldException.class, () -> pessoaService.insert(dto));
    }

    @Test
    public void insertShouldThrowInvalidFieldExceptionWhenApelidoHasMoreThan32Characters() {
        PessoaDTO dto = pessoaService.insert(Factory.createPessoaDTO());
        dto.setApelido("meuApelidoÉMuitoMuitoMuitoMuitoMuitoMuitoGrande");

        Assertions.assertThrows(InvalidFieldException.class, () -> pessoaService.insert(dto));
    }

    @Test
    public void insertShouldThrowInvalidFieldExceptionWhenNomeIsNull() {
        PessoaDTO dto = pessoaService.insert(Factory.createPessoaDTO());
        dto.setNome(null);

        Assertions.assertThrows(InvalidFieldException.class, () -> pessoaService.insert(dto));
    }
}
