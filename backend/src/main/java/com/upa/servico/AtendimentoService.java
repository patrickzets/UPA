package com.upa.servico;

import com.upa.dto.ChamadaDto;
import com.upa.dto.ChegadaRequest;
import com.upa.dto.EstadoFilaDto;
import com.upa.estruturas.Fila;
import com.upa.estruturas.FilaPrioridade;
import com.upa.excecao.EstruturaVaziaException;
import com.upa.excecao.RegraNegocioException;
import com.upa.modelo.Atendimento;
import com.upa.modelo.Consultorio;
import com.upa.modelo.Paciente;
import com.upa.modelo.Sintoma;
import com.upa.modelo.StatusPaciente;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AtendimentoService {

    private final Fila<Paciente> recepcao = new Fila<>();
    private final FilaPrioridade<Paciente> aguardando = new FilaPrioridade<>(comparadorDePrioridade());
    private final PacienteService pacienteService;
    private final SintomaService sintomaService;
    private final ConsultorioService consultorioService;
    private final HistoricoService historicoService;

    public AtendimentoService(PacienteService pacienteService, SintomaService sintomaService,
                              ConsultorioService consultorioService, HistoricoService historicoService) {
        this.pacienteService = pacienteService;
        this.sintomaService = sintomaService;
        this.consultorioService = consultorioService;
        this.historicoService = historicoService;
    }

    private static Comparator<Paciente> comparadorDePrioridade() {
        return (a, b) -> {
            int porCor = Integer.compare(b.getCor().getNivel(), a.getCor().getNivel());
            if (porCor != 0) {
                return porCor;
            }
            return Integer.compare(b.getPontuacao(), a.getPontuacao());
        };
    }

    public synchronized Paciente registrarChegada(ChegadaRequest dados) {
        Paciente paciente = pacienteService.buscar(dados.cpf());
        if (paciente.getStatus() != StatusPaciente.CADASTRADO && paciente.getStatus() != StatusPaciente.ATENDIDO) {
            throw new RegraNegocioException(paciente.getNome() + " ja esta em " + paciente.getStatus());
        }
        int pontos = 0;
        List<String> nomes = new ArrayList<>();
        for (Integer id : dados.sintomas()) {
            Sintoma sintoma = sintomaService.buscar(id);
            pontos += sintoma.pontos();
            nomes.add(sintoma.nome());
        }
        paciente.setCor(dados.cor());
        paciente.setPontuacao(pontos);
        paciente.setSintomas(nomes);
        paciente.setChegadaEm(LocalDateTime.now());
        paciente.setStatus(StatusPaciente.NA_RECEPCAO);
        recepcao.enqueue(paciente);
        return paciente;
    }

    public synchronized Paciente triarProximo() {
        if (recepcao.isEmpty()) {
            throw new EstruturaVaziaException("A fila da recepcao");
        }
        Paciente paciente = recepcao.dequeue();
        paciente.setStatus(StatusPaciente.AGUARDANDO);
        aguardando.inserir(paciente);
        return paciente;
    }

    public synchronized int triarTodos() {
        int total = 0;
        while (!recepcao.isEmpty()) {
            triarProximo();
            total++;
        }
        return total;
    }

    public synchronized Paciente consultarProximo() {
        return aguardando.proximo();
    }

    public synchronized ChamadaDto chamarProximo() {
        if (aguardando.isEmpty()) {
            throw new EstruturaVaziaException("A fila de atendimento");
        }
        Consultorio consultorio = consultorioService.reservarProximoLivre();
        if (consultorio == null) {
            throw new RegraNegocioException("Nenhum consultorio disponivel, o paciente continua aguardando");
        }
        Paciente paciente = aguardando.retirar();
        if (paciente.getStatus() != StatusPaciente.AGUARDANDO) {
            throw new RegraNegocioException(paciente.getNome() + " nao esta aguardando atendimento");
        }
        paciente.setStatus(StatusPaciente.EM_ATENDIMENTO);
        consultorioService.iniciarAtendimento(consultorio, paciente);
        return new ChamadaDto(paciente, consultorio.getNumero());
    }

    public synchronized Atendimento concluir(int numeroConsultorio) {
        ConsultorioService.SessaoEncerrada sessao = consultorioService.encerrarAtendimento(numeroConsultorio);
        sessao.paciente().setStatus(StatusPaciente.ATENDIDO);
        return historicoService.registrar(sessao.paciente(), sessao.consultorio(), sessao.inicio());
    }

    public synchronized EstadoFilaDto estado() {
        List<Paciente> emAtendimento = new ArrayList<>();
        for (Consultorio c : consultorioService.listar()) {
            if (c.isOcupado()) {
                emAtendimento.add(c.getPacienteAtual());
            }
        }
        Paciente proximo = aguardando.isEmpty() ? null : aguardando.proximo();
        return new EstadoFilaDto(recepcao.percorrer(), aguardando.emOrdemDeAtendimento(), proximo, emAtendimento);
    }
}
