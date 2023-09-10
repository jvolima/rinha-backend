package com.jvolima.rinhabackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.services.PessoaService;
import com.jvolima.rinhabackend.services.exceptions.BadRequestException;
import com.jvolima.rinhabackend.services.exceptions.NotFoundException;
import com.jvolima.rinhabackend.services.exceptions.UnprocessableEntityException;
import com.jvolima.rinhabackend.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

@WebMvcTest(PessoaController.class)
public class PessoaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PessoaService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Long count;
    private String searchTerm;
    private UUID existingId;
    private UUID nonExistingId;
    private PessoaDTO dto;

    @BeforeEach
    public void setUp() {
        count = 10L;
        searchTerm = "fulano";
        existingId = UUID.randomUUID();
        nonExistingId = UUID.randomUUID();

        dto = Factory.createPessoaDTO();
        dto.setId(UUID.randomUUID());

        Mockito.when(service.count()).thenReturn(count);

        Mockito.when(service.findAllBySubstring(searchTerm)).thenReturn(Factory.createPessoaDTOList());
        Mockito.when(service.findAllBySubstring(null)).thenThrow(BadRequestException.class);

        Mockito.when(service.findById(existingId)).thenReturn(dto);
        Mockito.when(service.findById(nonExistingId)).thenThrow(NotFoundException.class);

        Mockito.when(service.insert(ArgumentMatchers.any(PessoaDTO.class))).thenReturn(dto);
    }

    @Test
    public void countShouldReturnOkAndTheNumberOfPessoaInTheDatabase() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/contagem-pessoas")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$").value(count));
    }

    @Test
    public void findAllBySubstringShouldReturnOkAndAListOfPessoaDTOThatHaveTheSearchTerm() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/pessoas?t={searchTerm}", searchTerm)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").exists());
    }

    @Test
    public void findAllBySubstringShouldReturnBadRequestWhenSearchTermIsNotPresent() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/pessoas")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void findByIdShouldReturnOkAndPessoaDTOWhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/pessoas/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/pessoas/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
    }

    @Test
    public void insertShouldReturnCreatedWhenDataIsValid() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(Factory.createPessoaDTO());

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.header().string("Location", "http://localhost/pessoas/" + dto.getId()));
        result.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenApelidoIsNull() throws Exception {
        PessoaDTO dto = Factory.createPessoaDTO();
        dto.setApelido(null);

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Campo obrigatório"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenApelidoHasMoreThan32Characters() throws Exception {
        PessoaDTO dto = Factory.createPessoaDTO();
        dto.setApelido("MeuApelidoÉMuitoMuitoMuitoMuitoGrande");

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Máximo de 32 caracteres"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenNomeIsNull() throws Exception {
        PessoaDTO dto = Factory.createPessoaDTO();
        dto.setNome(null);

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Campo obrigatório"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenNomeHasMoreThan100Characters() throws Exception {
        PessoaDTO dto = Factory.createPessoaDTO();
        dto.setNome("MeuNomeÉMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoGrande");

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Máximo de 100 caracteres"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenNascimentoIsNull() throws Exception {
        PessoaDTO dto = Factory.createPessoaDTO();
        dto.setNascimento(null);

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Campo obrigatório"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenNascimentoIsOutOfDesiredFormat() throws Exception {
        PessoaDTO dto = Factory.createPessoaDTO();
        dto.setNascimento("10/11/2002");

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Formato desejado: AAAA-MM-DD (ano, mês, dia)"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenSomeStackIsNull() throws Exception {
        PessoaDTO dto = Factory.createPessoaDTO();
        dto.getStack().clear();
        dto.getStack().add(null);

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Campo obrigatório"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenSomeStackHasMoreThan32Characters() throws Exception {
        PessoaDTO dto = Factory.createPessoaDTO();
        dto.getStack().add("MinhaStackÉTãoBoaQueTemEsseNomeGrande");

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Máximo de 32 caracteres"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenServiceThrowUnprocessableEntityException() throws Exception {
        PessoaDTO unprocessableDTO = Factory.createPessoaDTO();

        String jsonBody = objectMapper.writeValueAsString(unprocessableDTO);

        Mockito.when(service.insert(ArgumentMatchers.any(PessoaDTO.class))).thenThrow(UnprocessableEntityException.class);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnBadRequestWhenServiceThrowBadRequestException() throws Exception {
        PessoaDTO badDTO = Factory.createPessoaDTO();

        String jsonBody = objectMapper.writeValueAsString(badDTO);

        Mockito.when(service.insert(ArgumentMatchers.any(PessoaDTO.class))).thenThrow(BadRequestException.class);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
