package com.upa.estruturas;

import com.upa.excecao.EstruturaVaziaException;

import java.util.ArrayList;
import java.util.List;

public class Fila<T> {

    private static class No<T> {
        T valor;
        No<T> proximo;

        No(T valor) {
            this.valor = valor;
        }
    }

    private No<T> inicio;
    private No<T> fim;
    private int tamanho;

    public void enqueue(T valor) {
        No<T> novo = new No<>(valor);
        if (fim == null) {
            inicio = novo;
        } else {
            fim.proximo = novo;
        }
        fim = novo;
        tamanho++;
    }

    public T dequeue() {
        if (isEmpty()) {
            throw new EstruturaVaziaException("A fila");
        }
        T valor = inicio.valor;
        inicio = inicio.proximo;
        if (inicio == null) {
            fim = null;
        }
        tamanho--;
        return valor;
    }

    public T peek() {
        if (isEmpty()) {
            throw new EstruturaVaziaException("A fila");
        }
        return inicio.valor;
    }

    public boolean isEmpty() {
        return inicio == null;
    }

    public int tamanho() {
        return tamanho;
    }

    public List<T> percorrer() {
        List<T> resultado = new ArrayList<>();
        No<T> atual = inicio;
        while (atual != null) {
            resultado.add(atual.valor);
            atual = atual.proximo;
        }
        return resultado;
    }
}
