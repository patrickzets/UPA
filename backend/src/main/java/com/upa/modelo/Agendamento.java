package com.upa.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Agendamento implements Comparable<Agendamento> {

    private final int id;
    private final String cpf;
    private final LocalDate data;
    private final LocalTime horario;
    private final String especialidade;

    public Agendamento(int id, String cpf, LocalDate data, LocalTime horario, String especialidade) {
        this.id = id;
        this.cpf = cpf;
        this.data = data;
        this.horario = horario;
        this.especialidade = especialidade;
    }

    public int getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public LocalDateTime dataHora() {
        return LocalDateTime.of(data, horario);
    }

    @Override
    public int compareTo(Agendamento outro) {
        int resultado = dataHora().compareTo(outro.dataHora());
        if (resultado != 0) {
            return resultado;
        }
        return Integer.compare(id, outro.id);
    }
}
