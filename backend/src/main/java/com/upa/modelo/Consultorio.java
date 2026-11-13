package com.upa.modelo;

import java.time.Instant;
import java.time.LocalDateTime;

public class Consultorio {

    private final int numero;
    private boolean aberto = true;
    private boolean naFila;
    private Paciente pacienteAtual;
    private LocalDateTime inicioAtendimento;
    private Instant reabreEm;

    public Consultorio(int numero) {
        this.numero = numero;
    }

    public int getNumero() {
        return numero;
    }

    public boolean isAberto() {
        return aberto;
    }

    public void setAberto(boolean aberto) {
        this.aberto = aberto;
    }

    public boolean isNaFila() {
        return naFila;
    }

    public void setNaFila(boolean naFila) {
        this.naFila = naFila;
    }

    public Paciente getPacienteAtual() {
        return pacienteAtual;
    }

    public void setPacienteAtual(Paciente pacienteAtual) {
        this.pacienteAtual = pacienteAtual;
    }

    public LocalDateTime getInicioAtendimento() {
        return inicioAtendimento;
    }

    public void setInicioAtendimento(LocalDateTime inicioAtendimento) {
        this.inicioAtendimento = inicioAtendimento;
    }

    public Instant getReabreEm() {
        return reabreEm;
    }

    public void setReabreEm(Instant reabreEm) {
        this.reabreEm = reabreEm;
    }

    public boolean isOcupado() {
        return pacienteAtual != null;
    }
}
