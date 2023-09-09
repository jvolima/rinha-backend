package com.jvolima.rinhabackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvolima.rinhabackend.dto.PessoaDTO;
import com.jvolima.rinhabackend.services.PessoaService;
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

    private PessoaDTO dto;

    @BeforeEach
    public void setUp() {
        dto = Factory.createPessoaDTO();
        dto.setId(UUID.randomUUID());

        Mockito.when(service.insert(ArgumentMatchers.any())).thenReturn(dto);
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
}
