package com.jvolima.rinhabackend.dto;

import com.jvolima.rinhabackend.entities.Pessoa;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PessoaDTO {

    private UUID id;
    private String apelido;
    private String nome;
    private String nascimento;
    private Set<String> stack = new HashSet<>();

    public PessoaDTO() {
    }

    public PessoaDTO(UUID id, String apelido, String nome, String nascimento) {
        this.id = id;
        this.apelido = apelido;
        this.nome = nome;
        this.nascimento = nascimento;
    }

    public PessoaDTO(Pessoa entity) {
        id = entity.getId();
        apelido = entity.getApelido();
        nome = entity.getNome();
        nascimento = entity.getNascimento();
        stack = entity.getStack();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNascimento() {
        return nascimento;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public Set<String> getStack() {
        return stack;
    }
}
