package com.upa.servico;

import com.upa.estruturas.Pilha;
import com.upa.excecao.EstruturaVaziaException;
import com.upa.modelo.Operacao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperacoesService {

    private final Pilha<Operacao> pilha = new Pilha<>();

    public synchronized void registrar(String descricao, Runnable reverter) {
        pilha.push(new Operacao(descricao, reverter));
    }

    public String desfazer() {
        Operacao operacao = retirarTopo();
        operacao.reverter();
        return "Desfeito: " + operacao.getDescricao();
    }

    private synchronized Operacao retirarTopo() {
        if (pilha.isEmpty()) {
            throw new EstruturaVaziaException("A pilha de operacoes");
        }
        return pilha.pop();
    }

    public synchronized Operacao ultima() {
        return pilha.peek();
    }

    public synchronized List<Operacao> listar() {
        return pilha.doTopoAteBase();
    }

    public synchronized void limpar() {
        while (!pilha.isEmpty()) {
            pilha.pop();
        }
    }
}
