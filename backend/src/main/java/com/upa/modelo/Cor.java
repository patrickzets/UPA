package com.upa.modelo;

public enum Cor {
    VERMELHO(3),
    AMARELO(2),
    VERDE(1);

    private final int nivel;

    Cor(int nivel) {
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }
}
