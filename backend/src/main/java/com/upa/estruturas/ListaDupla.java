package com.upa.estruturas;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ListaDupla<T> {

    public static class No<T> {
        private final T valor;
        private No<T> proximo;
        private No<T> anterior;

        No(T valor) {
            this.valor = valor;
        }

        public T getValor() {
            return valor;
        }

        public No<T> getProximo() {
            return proximo;
        }

        public No<T> getAnterior() {
            return anterior;
        }
    }

    private No<T> inicio;
    private No<T> fim;
    private int tamanho;

    public void inserirFim(T valor) {
        No<T> novo = new No<>(valor);
        if (fim == null) {
            inicio = novo;
            fim = novo;
        } else {
            novo.anterior = fim;
            fim.proximo = novo;
            fim = novo;
        }
        tamanho++;
    }

    public void inserirInicio(T valor) {
        No<T> novo = new No<>(valor);
        if (inicio == null) {
            inicio = novo;
            fim = novo;
        } else {
            novo.proximo = inicio;
            inicio.anterior = novo;
            inicio = novo;
        }
        tamanho++;
    }

    public No<T> buscarNo(Predicate<T> criterio) {
        No<T> atual = inicio;
        while (atual != null) {
            if (criterio.test(atual.valor)) {
                return atual;
            }
            atual = atual.proximo;
        }
        return null;
    }

    public boolean remover(Predicate<T> criterio) {
        No<T> alvo = buscarNo(criterio);
        if (alvo == null) {
            return false;
        }
        if (alvo.anterior == null) {
            inicio = alvo.proximo;
        } else {
            alvo.anterior.proximo = alvo.proximo;
        }
        if (alvo.proximo == null) {
            fim = alvo.anterior;
        } else {
            alvo.proximo.anterior = alvo.anterior;
        }
        tamanho--;
        return true;
    }

    public List<T> percorrerInicioFim() {
        List<T> resultado = new ArrayList<>();
        No<T> atual = inicio;
        while (atual != null) {
            resultado.add(atual.valor);
            atual = atual.proximo;
        }
        return resultado;
    }

    public List<T> percorrerFimInicio() {
        List<T> resultado = new ArrayList<>();
        No<T> atual = fim;
        while (atual != null) {
            resultado.add(atual.valor);
            atual = atual.anterior;
        }
        return resultado;
    }

    public No<T> getInicio() {
        return inicio;
    }

    public No<T> getFim() {
        return fim;
    }

    public boolean estaVazia() {
        return inicio == null;
    }

    public int tamanho() {
        return tamanho;
    }
}
