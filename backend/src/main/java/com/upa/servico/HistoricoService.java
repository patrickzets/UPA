package com.upa.servico;

import com.upa.estruturas.ListaDupla;
import com.upa.excecao.EstruturaVaziaException;
import com.upa.excecao.NaoEncontradoException;
import com.upa.excecao.RegraNegocioException;
import com.upa.modelo.Atendimento;
import com.upa.modelo.Paciente;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoricoService {

    private final ListaDupla<Atendimento> historico = new ListaDupla<>();
    private ListaDupla.No<Atendimento> cursor;
    private int proximoId = 1;

    public synchronized Atendimento registrar(Paciente paciente, int consultorio, LocalDateTime inicio) {
        Atendimento atendimento = new Atendimento(proximoId++, paciente, consultorio, inicio, LocalDateTime.now());
        historico.inserirFim(atendimento);
        return atendimento;
    }

    public synchronized List<Atendimento> listar(boolean decrescente) {
        return decrescente ? historico.percorrerFimInicio() : historico.percorrerInicioFim();
    }

    public synchronized void remover(int id) {
        ListaDupla.No<Atendimento> no = historico.buscarNo(a -> a.getId() == id);
        if (no == null) {
            throw new NaoEncontradoException("Atendimento " + id + " nao esta no historico");
        }
        if (no == cursor) {
            cursor = null;
        }
        historico.remover(a -> a.getId() == id);
    }

    public synchronized Atendimento irParaInicio() {
        exigirNaoVazio();
        cursor = historico.getInicio();
        return cursor.getValor();
    }

    public synchronized Atendimento irParaFim() {
        exigirNaoVazio();
        cursor = historico.getFim();
        return cursor.getValor();
    }

    public synchronized Atendimento proximo() {
        exigirNaoVazio();
        if (cursor == null) {
            return irParaInicio();
        }
        if (cursor.getProximo() == null) {
            throw new RegraNegocioException("Voce ja esta no ultimo registro do historico");
        }
        cursor = cursor.getProximo();
        return cursor.getValor();
    }

    public synchronized Atendimento anterior() {
        exigirNaoVazio();
        if (cursor == null) {
            return irParaFim();
        }
        if (cursor.getAnterior() == null) {
            throw new RegraNegocioException("Voce ja esta no primeiro registro do historico");
        }
        cursor = cursor.getAnterior();
        return cursor.getValor();
    }

    private void exigirNaoVazio() {
        if (historico.estaVazia()) {
            throw new EstruturaVaziaException("O historico");
        }
    }
}
