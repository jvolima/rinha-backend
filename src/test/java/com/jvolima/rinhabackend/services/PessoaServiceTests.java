package com.jvolima.rinhabackend.services;

import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.entities.Pessoa;
import com.jvolima.rinhabackend.repositories.PessoaRepository;
import com.jvolima.rinhabackend.services.exceptions.BadRequestException;
import com.jvolima.rinhabackend.services.exceptions.NotFoundException;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
public class PessoaServiceTests {

    @InjectMocks
    private PessoaService pessoaService;

    @Mock
    private PessoaRepository pessoaRepository;

    private Long totalPessoa;
    private String searchTerm;
    private String apelido;
    private UUID existingId;
    private UUID nonExistingId;

    @BeforeEach
    public void setUp() {
        totalPessoa = 5L;
        searchTerm = "fulano";
        apelido = "anyApelido";
        existingId = UUID.randomUUID();
        nonExistingId = UUID.randomUUID();

        Mockito.when(pessoaRepository.count()).thenReturn(totalPessoa);

        Mockito.when(pessoaRepository.findAllBySubstring(searchTerm)).thenReturn(Factory.createPessoaList());

        Mockito.when(pessoaRepository.findById(existingId)).thenReturn(Optional.of(Factory.createPessoa()));
        Mockito.when(pessoaRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(pessoaRepository.findByApelido(apelido)).thenReturn(Factory.createPessoa());

        Mockito.when(pessoaRepository.save(ArgumentMatchers.any(Pessoa.class))).thenReturn(Factory.createPessoa());
    }

    @Test
    public void countShouldReturnTheNumberOfPessoaInTheDatabase() {
        Long count = pessoaService.count();

        Assertions.assertEquals(totalPessoa, count);
        Mockito.verify(pessoaRepository).count();
    }

    @Test
    public void findAllBySubstringShouldReturnAListOfPessoaDTOThatHaveTheSearchTerm() {
        List<PessoaDTO> list = pessoaService.findAllBySubstring(searchTerm);

        Assertions.assertEquals(Factory.createPessoaList().size(), list.size());
        Mockito.verify(pessoaRepository).findAllBySubstring(searchTerm);
    }

    @Test
    public void findAllBySubstringShouldThrowBadRequestExceptionWhenSearchTermIsNull() {
        Assertions.assertThrows(BadRequestException.class, () -> pessoaService.findAllBySubstring(null));
    }

    @Test
    public void findByIdShouldReturnPessoaDTOWhenIdExists() {
        PessoaDTO dto = pessoaService.findById(existingId);

        Assertions.assertNotNull(dto);
        Mockito.verify(pessoaRepository).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(NotFoundException.class, () -> pessoaService.findById(nonExistingId));
        Mockito.verify(pessoaRepository).findById(nonExistingId);
    }

    @Test
    public void insertShouldReturnPessoaDTOWhenDataIsValid() {
        PessoaDTO dto1 = pessoaService.insert(Factory.createPessoaDTO());
        PessoaDTO dto2 = Factory.createPessoaDTO();
        dto2.setNullForStack();
        dto2.setApelido("outroApelido");
        dto2 = pessoaService.insert(dto2);

        Assertions.assertNotNull(dto1);
        Assertions.assertNotNull(dto2);
    }

    @Test
    public void insertShouldThrowBadRequestExceptionWhenApelidoHasOnlyNumbers() {
        PessoaDTO dto = Factory.createPessoaDTO();
        dto.setApelido("15");

        Assertions.assertThrows(BadRequestException.class, () -> pessoaService.insert(dto));
    }

    @Test
    public void insertShouldThrowBadRequestExceptionWhenNomeHasOnlyNumbers() {
        PessoaDTO dto = Factory.createPessoaDTO();
        dto.setNome("80");

        Assertions.assertThrows(BadRequestException.class, () -> pessoaService.insert(dto));
    }

    @Test
    public void insertShouldThrowBadRequestWhenSomeStackHasOnlyNumbers() {
        PessoaDTO dto = Factory.createPessoaDTO();
        dto.getStack().add("120");

        Assertions.assertThrows(BadRequestException.class, () -> pessoaService.insert(dto));
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

    @Test
    public void insertShouldThrowUnprocessableEntityExceptionWhenApelidoAlreadyExist() {
        PessoaDTO dto = Factory.createPessoaDTO();
        dto.setApelido(apelido);

        Assertions.assertThrows(UnprocessableEntityException.class, () -> pessoaService.insert(dto));
    }
}
