package com.jvolima.rinhabackend.services;

import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.entities.Pessoa;
import com.jvolima.rinhabackend.repositories.PessoaRepository;
import com.jvolima.rinhabackend.services.exceptions.UnprocessableEntityException;
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

import java.time.LocalDate;

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
    public void insertShouldThrowUnprocessableEntityExceptionWhenNascimentoHasInvalidDay() {
        PessoaDTO dto = Factory.createPessoaDTO();
        dto.setNascimento("2004-10-34");

        Assertions.assertThrows(UnprocessableEntityException.class, () -> pessoaService.insert(dto));
    }

    @Test
    public void insertShouldThrowUnprocessableEntityExceptionWhenNascimentoHasInvalidMonth() {
        PessoaDTO dto = Factory.createPessoaDTO();
        dto.setNascimento("2004-14-20");

        Assertions.assertThrows(UnprocessableEntityException.class, () -> pessoaService.insert(dto));
    }

    @Test
    public void insertShouldThrowUnprocessableEntityExceptionWhenNascimentoIsAfterNow() {
        PessoaDTO dto = Factory.createPessoaDTO();
        LocalDate currentDate = LocalDate.now();
        LocalDate dateOneYearLater = currentDate.plusYears(1);

        dto.setNascimento(dateOneYearLater.toString());

        Assertions.assertThrows(UnprocessableEntityException.class, () -> pessoaService.insert(dto));
    }
}
