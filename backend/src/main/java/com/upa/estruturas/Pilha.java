package com.upa.estruturas;

import com.upa.excecao.EstruturaVaziaException;

import java.util.ArrayList;
import java.util.List;

public class Pilha<T> {

    private static class No<T> {
        T valor;
        No<T> abaixo;

        No(T valor) {
            this.valor = valor;
        }
    }

    private No<T> topo;
    private int tamanho;

    public void push(T valor) {
        No<T> novo = new No<>(valor);
        novo.abaixo = topo;
        topo = novo;
        tamanho++;
    }

    public T pop() {
        if (isEmpty()) {
            throw new EstruturaVaziaException("A pilha");
        }
        T valor = topo.valor;
        topo = topo.abaixo;
        tamanho--;
        return valor;
    }

    public T peek() {
        if (isEmpty()) {
            throw new EstruturaVaziaException("A pilha");
        }
        return topo.valor;
    }

    public boolean isEmpty() {
        return topo == null;
    }

    public int tamanho() {
        return tamanho;
    }

    public List<T> doTopoAteBase() {
        List<T> resultado = new ArrayList<>();
        No<T> atual = topo;
        while (atual != null) {
            resultado.add(atual.valor);
            atual = atual.abaixo;
        }
        return resultado;
    }
}
