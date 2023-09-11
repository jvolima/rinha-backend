package com.jvolima.rinhabackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvolima.rinhabackend.config.IntegrationTestsSeed;
import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles(profiles = "test")
public class PessoaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long count;
    private String searchTerm;
    private UUID existingId;
    private UUID nonExistingId;
    private String invalidId;
    private PessoaDTO dto;

    @BeforeEach
    void setUp() throws Exception {
        count = 2L;
        searchTerm = "Spring";
        existingId = IntegrationTestsSeed.pessoaId;
        nonExistingId = UUID.randomUUID();
        invalidId = "invalidId";
        dto = Factory.createPessoaDTO();
    }

    @Test
    public void countShouldReturnTotalPessoaInDatabase() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/contagem-pessoas")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$").value(count));
    }

    @Test
    public void findAllBySubstringShouldReturnAListOfPessoaDTOThatHaveTheSearchTerm() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/pessoas?t={searchTerm}", searchTerm)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.[0].apelido").value("alice"));
    }

    @Test
    public void findAllBySubstringShouldReturnBadRequestWhenSearchTermIsNotPresentInURL() throws Exception {
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
        result.andExpect(MockMvcResultMatchers.jsonPath("$.apelido").value("johndoe"));
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result1 =
                mockMvc.perform(MockMvcRequestBuilders.get("/pessoas/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        result1.andExpect(MockMvcResultMatchers.status().isNotFound());
        result1.andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());

        ResultActions result2 =
                mockMvc.perform(MockMvcRequestBuilders.get("/pessoas/{id}", invalidId)
                        .accept(MediaType.APPLICATION_JSON));

        result2.andExpect(MockMvcResultMatchers.status().isNotFound());
        result2.andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
    }

    @Test
    public void insertShouldReturnCreatedWhenDataIsValid() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(Factory.createPessoaDTO());

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenApelidoIsNull() throws Exception {
        dto.setApelido(null);

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].fieldName").value("apelido"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Campo obrigatório"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenApelidoHasMoreThan32Characters() throws Exception {
        dto.setApelido("MeuApelidoÉMuitoMuitoMuitoMuitoGrande");

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].fieldName").value("apelido"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Máximo de 32 caracteres"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenNomeIsNull() throws Exception {
        dto.setNome(null);

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].fieldName").value("nome"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Campo obrigatório"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenNomeHasMoreThan100Characters() throws Exception {
        dto.setNome("MeuNomeÉMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoMuitoGrande");

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].fieldName").value("nome"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Máximo de 100 caracteres"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenNascimentoIsNull() throws Exception {
        dto.setNascimento(null);

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].fieldName").value("nascimento"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Campo obrigatório"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenNascimentoIsOutOfDesiredFormat() throws Exception {
        dto.setNascimento("10/11/2002");

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].fieldName").value("nascimento"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Formato desejado: AAAA-MM-DD (ano, mês, dia)"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenNascimentoIsInvalidDate() throws Exception {
        dto.setNascimento("2030-15-32");

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Data de nascimento inválida"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenSomeStackIsNull() throws Exception {
        dto.getStack().clear();
        dto.getStack().add(null);

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].fieldName").value("stack[]"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Campo obrigatório"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenSomeStackHasMoreThan32Characters() throws Exception {
        dto.getStack().add("MinhaStackÉTãoBoaQueTemEsseNomeGrande");

        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/pessoas")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].fieldName").value("stack[]"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Máximo de 32 caracteres"));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }
}
