package com.upa.estruturas;

import com.upa.excecao.EstruturaVaziaException;
import com.upa.excecao.FilaCheiaException;

import java.util.ArrayList;
import java.util.List;

public class FilaCircular<T> {

    private final Object[] vetor;
    private int inicio;
    private int fim;
    private int tamanho;

    public FilaCircular(int capacidade) {
        this.vetor = new Object[capacidade];
    }

    public void enqueue(T valor) {
        if (isFull()) {
            throw new FilaCheiaException(vetor.length);
        }
        vetor[fim] = valor;
        fim = (fim + 1) % vetor.length;
        tamanho++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            throw new EstruturaVaziaException("A fila circular");
        }
        T valor = (T) vetor[inicio];
        vetor[inicio] = null;
        inicio = (inicio + 1) % vetor.length;
        tamanho--;
        return valor;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new EstruturaVaziaException("A fila circular");
        }
        return (T) vetor[inicio];
    }

    public boolean isEmpty() {
        return tamanho == 0;
    }

    public boolean isFull() {
        return tamanho == vetor.length;
    }

    public int tamanho() {
        return tamanho;
    }

    public int capacidade() {
        return vetor.length;
    }

    public int getInicio() {
        return inicio;
    }

    public int getFim() {
        return fim;
    }

    @SuppressWarnings("unchecked")
    public List<T> percorrer() {
        List<T> resultado = new ArrayList<>();
        for (int i = 0; i < tamanho; i++) {
            resultado.add((T) vetor[(inicio + i) % vetor.length]);
        }
        return resultado;
    }

    public List<String> estadoDoVetor() {
        List<String> posicoes = new ArrayList<>();
        for (Object item : vetor) {
            posicoes.add(item == null ? "vazio" : item.toString());
        }
        return posicoes;
    }
}
