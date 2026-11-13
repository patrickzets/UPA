package com.upa.excecao;

public class EstruturaVaziaException extends RuntimeException {

    public EstruturaVaziaException(String estrutura) {
        super(estrutura + " esta vazia");
    }
}
