package com.upa.servico;

import com.upa.dto.AgendamentoRequest;
import com.upa.estruturas.ListaOrdenada;
import com.upa.excecao.NaoEncontradoException;
import com.upa.excecao.RegraNegocioException;
import com.upa.modelo.Agendamento;
import com.upa.modelo.Paciente;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendamentoService {

    private final ListaOrdenada<Agendamento> agenda = new ListaOrdenada<>();
    private final PacienteService pacienteService;
    private final OperacoesService operacoes;
    private int proximoId = 1;

    public AgendamentoService(PacienteService pacienteService, OperacoesService operacoes) {
        this.pacienteService = pacienteService;
        this.operacoes = operacoes;
    }

    public synchronized Agendamento agendar(AgendamentoRequest dados) {
        Paciente paciente = pacienteService.buscar(dados.cpf());
        if (LocalDateTime.of(dados.data(), dados.horario()).isBefore(LocalDateTime.now())) {
            throw new RegraNegocioException("Nao e possivel agendar no passado");
        }
        String especialidade = dados.especialidade().trim();
        Agendamento conflito = agenda.buscar(a -> a.getData().equals(dados.data())
                && a.getHorario().equals(dados.horario())
                && a.getEspecialidade().equalsIgnoreCase(especialidade));
        if (conflito != null) {
            throw new RegraNegocioException("Ja existe consulta de " + especialidade + " nesse horario");
        }
        Agendamento agendamento = new Agendamento(proximoId++, paciente.getCpf(), dados.data(),
                dados.horario(), especialidade);
        agenda.inserir(agendamento);
        int id = agendamento.getId();
        operacoes.registrar("Agendamento de " + especialidade + " para " + paciente.getNome(),
                () -> agenda.remover(a -> a.getId() == id));
        return agendamento;
    }

    public synchronized List<Agendamento> listar() {
        return agenda.exibir();
    }

    public synchronized void remover(int id) {
        if (!agenda.remover(a -> a.getId() == id)) {
            throw new NaoEncontradoException("Agendamento " + id + " nao encontrado");
        }
    }
}
