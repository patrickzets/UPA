package com.upa.excecao;

public class FilaCheiaException extends RuntimeException {

    public FilaCheiaException(int capacidade) {
        super("A fila circular esta cheia (capacidade " + capacidade + ")");
    }
}
