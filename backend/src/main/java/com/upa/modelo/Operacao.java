package com.upa.modelo;

import java.time.LocalDateTime;

public class Operacao {

    private final String descricao;
    private final LocalDateTime hora = LocalDateTime.now();
    private final Runnable reverter;

    public Operacao(String descricao, Runnable reverter) {
        this.descricao = descricao;
        this.reverter = reverter;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getHora() {
        return hora;
    }

    public void reverter() {
        reverter.run();
    }
}
