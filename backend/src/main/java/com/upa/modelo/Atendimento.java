package com.upa.modelo;

import java.time.LocalDateTime;

public class Atendimento {

    private final int id;
    private final String cpf;
    private final String nomePaciente;
    private final Cor cor;
    private final int pontuacao;
    private final int consultorio;
    private final LocalDateTime inicio;
    private final LocalDateTime fim;

    public Atendimento(int id, Paciente paciente, int consultorio, LocalDateTime inicio, LocalDateTime fim) {
        this.id = id;
        this.cpf = paciente.getCpf();
        this.nomePaciente = paciente.getNome();
        this.cor = paciente.getCor();
        this.pontuacao = paciente.getPontuacao();
        this.consultorio = consultorio;
        this.inicio = inicio;
        this.fim = fim;
    }

    public int getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public Cor getCor() {
        return cor;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public int getConsultorio() {
        return consultorio;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }
}
