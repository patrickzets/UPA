package com.upa;

import com.upa.estruturas.Fila;
import com.upa.estruturas.FilaCircular;
import com.upa.estruturas.FilaPrioridade;
import com.upa.estruturas.ListaDupla;
import com.upa.estruturas.ListaOrdenada;
import com.upa.estruturas.ListaSimples;
import com.upa.estruturas.Pilha;
import com.upa.excecao.EstruturaVaziaException;
import com.upa.excecao.FilaCheiaException;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstruturasTest {

    @Test
    void listaSimplesInsereBuscaERemove() {
        ListaSimples<String> lista = new ListaSimples<>();
        lista.inserirFim("a");
        lista.inserirFim("b");
        lista.inserirInicio("z");
        assertEquals(List.of("z", "a", "b"), lista.percorrer());
        assertEquals("a", lista.buscar(s -> s.equals("a")));
        assertNull(lista.buscar(s -> s.equals("x")));
        assertTrue(lista.remover(s -> s.equals("a")));
        assertFalse(lista.remover(s -> s.equals("a")));
        assertEquals(2, lista.tamanho());
    }

    @Test
    void listaDuplaPercorreNosDoisSentidos() {
        ListaDupla<Integer> lista = new ListaDupla<>();
        lista.inserirFim(1);
        lista.inserirFim(2);
        lista.inserirFim(3);
        assertEquals(List.of(1, 2, 3), lista.percorrerInicioFim());
        assertEquals(List.of(3, 2, 1), lista.percorrerFimInicio());
        lista.remover(n -> n == 2);
        assertEquals(List.of(3, 1), lista.percorrerFimInicio());
        lista.remover(n -> n == 3);
        lista.remover(n -> n == 1);
        assertTrue(lista.estaVazia());
    }

    @Test
    void listaOrdenadaMantemOrdemComInsercaoDesordenada() {
        ListaOrdenada<Integer> lista = new ListaOrdenada<>();
        lista.inserir(5);
        lista.inserir(1);
        lista.inserir(9);
        lista.inserir(3);
        assertEquals(List.of(1, 3, 5, 9), lista.exibir());
        lista.remover(n -> n == 5);
        assertEquals(List.of(1, 3, 9), lista.exibir());
    }

    @Test
    void pilhaEhLifoETrataVazia() {
        Pilha<String> pilha = new Pilha<>();
        assertThrows(EstruturaVaziaException.class, pilha::pop);
        assertThrows(EstruturaVaziaException.class, pilha::peek);
        pilha.push("a");
        pilha.push("b");
        assertEquals("b", pilha.peek());
        assertEquals("b", pilha.pop());
        assertEquals("a", pilha.pop());
        assertTrue(pilha.isEmpty());
    }

    @Test
    void filaEhFifoETrataVazia() {
        Fila<String> fila = new Fila<>();
        assertThrows(EstruturaVaziaException.class, fila::dequeue);
        fila.enqueue("a");
        fila.enqueue("b");
        assertEquals("a", fila.peek());
        assertEquals("a", fila.dequeue());
        assertEquals("b", fila.dequeue());
        assertTrue(fila.isEmpty());
    }

    @Test
    void filaCircularReaproveitaPosicoesETrataCheia() {
        FilaCircular<Integer> fila = new FilaCircular<>(3);
        fila.enqueue(1);
        fila.enqueue(2);
        fila.enqueue(3);
        assertTrue(fila.isFull());
        assertThrows(FilaCheiaException.class, () -> fila.enqueue(4));
        assertEquals(1, fila.dequeue());
        fila.enqueue(4);
        assertEquals(List.of("4", "2", "3"), fila.estadoDoVetor());
        assertEquals(List.of(2, 3, 4), fila.percorrer());
        fila.dequeue();
        fila.dequeue();
        fila.dequeue();
        assertThrows(EstruturaVaziaException.class, fila::dequeue);
    }

    @Test
    void filaPrioridadeRespeitaPrioridadeEChegada() {
        FilaPrioridade<int[]> fila = new FilaPrioridade<>(Comparator.comparingInt((int[] p) -> -p[0]));
        fila.inserir(new int[]{1, 1});
        fila.inserir(new int[]{3, 2});
        fila.inserir(new int[]{3, 3});
        fila.inserir(new int[]{2, 4});
        assertEquals(2, fila.retirar()[1]);
        assertEquals(3, fila.retirar()[1]);
        assertEquals(4, fila.retirar()[1]);
        assertEquals(1, fila.retirar()[1]);
        assertThrows(EstruturaVaziaException.class, fila::retirar);
    }
}
