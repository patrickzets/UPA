package com.upa.estruturas;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ListaOrdenada<T extends Comparable<T>> {

    private static class No<T> {
        T valor;
        No<T> proximo;

        No(T valor) {
            this.valor = valor;
        }
    }

    private No<T> inicio;
    private int tamanho;

    public void inserir(T valor) {
        No<T> novo = new No<>(valor);
        if (inicio == null || valor.compareTo(inicio.valor) < 0) {
            novo.proximo = inicio;
            inicio = novo;
        } else {
            No<T> atual = inicio;
            while (atual.proximo != null && atual.proximo.valor.compareTo(valor) <= 0) {
                atual = atual.proximo;
            }
            novo.proximo = atual.proximo;
            atual.proximo = novo;
        }
        tamanho++;
    }

    public T buscar(Predicate<T> criterio) {
        No<T> atual = inicio;
        while (atual != null) {
            if (criterio.test(atual.valor)) {
                return atual.valor;
            }
            atual = atual.proximo;
        }
        return null;
    }

    public boolean remover(Predicate<T> criterio) {
        No<T> anterior = null;
        No<T> atual = inicio;
        while (atual != null) {
            if (criterio.test(atual.valor)) {
                if (anterior == null) {
                    inicio = atual.proximo;
                } else {
                    anterior.proximo = atual.proximo;
                }
                tamanho--;
                return true;
            }
            anterior = atual;
            atual = atual.proximo;
        }
        return false;
    }

    public List<T> exibir() {
        List<T> resultado = new ArrayList<>();
        No<T> atual = inicio;
        while (atual != null) {
            resultado.add(atual.valor);
            atual = atual.proximo;
        }
        return resultado;
    }

    public boolean estaVazia() {
        return inicio == null;
    }

    public int tamanho() {
        return tamanho;
    }
}
