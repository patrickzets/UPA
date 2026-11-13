package com.upa.estruturas;

import com.upa.excecao.EstruturaVaziaException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FilaPrioridade<T> {

    private static class Item<T> {
        T valor;
        long chegada;

        Item(T valor, long chegada) {
            this.valor = valor;
            this.chegada = chegada;
        }
    }

    private final Comparator<T> comparador;
    private Item<T>[] heap;
    private int tamanho;
    private long contadorChegada;

    @SuppressWarnings("unchecked")
    public FilaPrioridade(Comparator<T> comparador) {
        this.comparador = comparador;
        this.heap = new Item[16];
    }

    public void inserir(T valor) {
        if (tamanho == heap.length) {
            aumentarVetor();
        }
        heap[tamanho] = new Item<>(valor, contadorChegada++);
        subir(tamanho);
        tamanho++;
    }

    public T proximo() {
        if (isEmpty()) {
            throw new EstruturaVaziaException("A fila de prioridade");
        }
        return heap[0].valor;
    }

    public T retirar() {
        if (isEmpty()) {
            throw new EstruturaVaziaException("A fila de prioridade");
        }
        T valor = heap[0].valor;
        tamanho--;
        heap[0] = heap[tamanho];
        heap[tamanho] = null;
        if (tamanho > 0) {
            descer(0);
        }
        return valor;
    }

    public boolean isEmpty() {
        return tamanho == 0;
    }

    public int tamanho() {
        return tamanho;
    }

    public List<T> emOrdemDeAtendimento() {
        FilaPrioridade<T> copia = new FilaPrioridade<>(comparador);
        copia.heap = java.util.Arrays.copyOf(heap, heap.length);
        copia.tamanho = tamanho;
        List<T> resultado = new ArrayList<>();
        while (!copia.isEmpty()) {
            resultado.add(copia.retirar());
        }
        return resultado;
    }

    private boolean vemAntes(Item<T> a, Item<T> b) {
        int resultado = comparador.compare(a.valor, b.valor);
        if (resultado != 0) {
            return resultado < 0;
        }
        return a.chegada < b.chegada;
    }

    private void subir(int i) {
        while (i > 0) {
            int pai = (i - 1) / 2;
            if (vemAntes(heap[i], heap[pai])) {
                trocar(i, pai);
                i = pai;
            } else {
                break;
            }
        }
    }

    private void descer(int i) {
        while (true) {
            int esquerda = 2 * i + 1;
            int direita = 2 * i + 2;
            int menor = i;
            if (esquerda < tamanho && vemAntes(heap[esquerda], heap[menor])) {
                menor = esquerda;
            }
            if (direita < tamanho && vemAntes(heap[direita], heap[menor])) {
                menor = direita;
            }
            if (menor == i) {
                break;
            }
            trocar(i, menor);
            i = menor;
        }
    }

    private void trocar(int a, int b) {
        Item<T> aux = heap[a];
        heap[a] = heap[b];
        heap[b] = aux;
    }

    private void aumentarVetor() {
        heap = java.util.Arrays.copyOf(heap, heap.length * 2);
    }
}
