package com.upa.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Paciente {

    private String cpf;
    private String nome;
    private int idade;
    private String descricao;
    private StatusPaciente status = StatusPaciente.CADASTRADO;
    private Cor cor;
    private int pontuacao;
    private List<String> sintomas = new ArrayList<>();
    private LocalDateTime chegadaEm;

    public Paciente(String cpf, String nome, int idade, String descricao) {
        this.cpf = cpf;
        this.nome = nome;
        this.idade = idade;
        this.descricao = descricao;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public int getIdade() {
        return idade;
    }

    public String getDescricao() {
        return descricao;
    }

    public StatusPaciente getStatus() {
        return status;
    }

    public void setStatus(StatusPaciente status) {
        this.status = status;
    }

    public Cor getCor() {
        return cor;
    }

    public void setCor(Cor cor) {
        this.cor = cor;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public List<String> getSintomas() {
        return sintomas;
    }

    public void setSintomas(List<String> sintomas) {
        this.sintomas = sintomas;
    }

    public LocalDateTime getChegadaEm() {
        return chegadaEm;
    }

    public void setChegadaEm(LocalDateTime chegadaEm) {
        this.chegadaEm = chegadaEm;
    }

    @Override
    public String toString() {
        return nome;
    }
}
