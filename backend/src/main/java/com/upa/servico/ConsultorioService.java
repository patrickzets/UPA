package com.upa.servico;

import com.upa.dto.FilaCircularDto;
import com.upa.estruturas.FilaCircular;
import com.upa.excecao.NaoEncontradoException;
import com.upa.excecao.RegraNegocioException;
import com.upa.modelo.Configuracao;
import com.upa.modelo.Consultorio;
import com.upa.modelo.Paciente;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ConsultorioService {

    public static final int TOTAL_CONSULTORIOS = 5;

    private final Consultorio[] consultorios = new Consultorio[TOTAL_CONSULTORIOS];
    private final FilaCircular<Integer> livres = new FilaCircular<>(TOTAL_CONSULTORIOS);
    private final List<String> eventos = new ArrayList<>();
    private final ConfiguracaoService configuracaoService;
    private final Random sorteio = new Random();
    private Instant ultimoFechamento = Instant.now();

    public ConsultorioService(ConfiguracaoService configuracaoService) {
        this.configuracaoService = configuracaoService;
        for (int i = 0; i < TOTAL_CONSULTORIOS; i++) {
            consultorios[i] = new Consultorio(i + 1);
            consultorios[i].setNaFila(true);
            livres.enqueue(i + 1);
        }
    }

    public synchronized List<Consultorio> listar() {
        return List.of(consultorios);
    }

    public synchronized FilaCircularDto estadoDaFila() {
        return new FilaCircularDto(livres.estadoDoVetor(), livres.getInicio(), livres.getFim(),
                livres.tamanho(), livres.capacidade());
    }

    public synchronized List<String> getEventos() {
        return new ArrayList<>(eventos);
    }

    public synchronized boolean temConsultorioDisponivel() {
        for (Consultorio c : consultorios) {
            if (c.isAberto() && !c.isOcupado()) {
                return true;
            }
        }
        return false;
    }

    public synchronized Consultorio reservarProximoLivre() {
        while (!livres.isEmpty()) {
            Consultorio c = obter(livres.dequeue());
            c.setNaFila(false);
            if (c.isAberto() && !c.isOcupado()) {
                return c;
            }
        }
        return null;
    }

    public synchronized void iniciarAtendimento(Consultorio c, Paciente paciente) {
        c.setPacienteAtual(paciente);
        c.setInicioAtendimento(LocalDateTime.now());
    }

    public synchronized SessaoEncerrada encerrarAtendimento(int numero) {
        Consultorio c = obter(numero);
        if (!c.isOcupado()) {
            throw new RegraNegocioException("O consultorio " + numero + " nao esta atendendo ninguem");
        }
        SessaoEncerrada sessao = new SessaoEncerrada(c.getPacienteAtual(), c.getNumero(), c.getInicioAtendimento());
        c.setPacienteAtual(null);
        c.setInicioAtendimento(null);
        if (c.isAberto()) {
            colocarNaFila(c);
        } else {
            registrar("Consultorio " + numero + " terminou o atendimento e continua fechado");
        }
        return sessao;
    }

    public synchronized void fechar(int numero, boolean manual) {
        Consultorio c = obter(numero);
        if (!c.isAberto()) {
            throw new RegraNegocioException("O consultorio " + numero + " ja esta fechado");
        }
        if (quantidadeAbertos() <= 1) {
            throw new RegraNegocioException("Pelo menos um consultorio precisa ficar aberto");
        }
        c.setAberto(false);
        if (manual) {
            c.setReabreEm(null);
            registrar("Consultorio " + numero + " foi fechado manualmente");
        } else {
            int duracao = configuracaoService.obter().getDuracaoFechamentoSeg();
            c.setReabreEm(Instant.now().plusSeconds(duracao));
            registrar("Consultorio " + numero + " fechou sozinho por " + duracao + "s");
        }
    }

    public synchronized void abrir(int numero) {
        Consultorio c = obter(numero);
        if (c.isAberto()) {
            throw new RegraNegocioException("O consultorio " + numero + " ja esta aberto");
        }
        c.setAberto(true);
        c.setReabreEm(null);
        registrar("Consultorio " + numero + " reabriu");
        if (!c.isOcupado()) {
            colocarNaFila(c);
        }
    }

    @Scheduled(fixedDelay = 1000)
    public synchronized void verificarFechamentos() {
        Instant agora = Instant.now();
        for (Consultorio c : consultorios) {
            if (!c.isAberto() && c.getReabreEm() != null && !agora.isBefore(c.getReabreEm())) {
                abrir(c.getNumero());
            }
        }
        Configuracao config = configuracaoService.obter();
        if (!config.isFechamentoAutomatico()) {
            ultimoFechamento = agora;
            return;
        }
        if (agora.isBefore(ultimoFechamento.plusSeconds(config.getIntervaloFechamentoSeg()))) {
            return;
        }
        ultimoFechamento = agora;
        if (quantidadeAbertos() > 1) {
            fechar(sortearAberto().getNumero(), false);
        }
    }

    private Consultorio sortearAberto() {
        List<Consultorio> abertos = new ArrayList<>();
        for (Consultorio c : consultorios) {
            if (c.isAberto()) {
                abertos.add(c);
            }
        }
        return abertos.get(sorteio.nextInt(abertos.size()));
    }

    private int quantidadeAbertos() {
        int total = 0;
        for (Consultorio c : consultorios) {
            if (c.isAberto()) {
                total++;
            }
        }
        return total;
    }

    private void colocarNaFila(Consultorio c) {
        if (!c.isNaFila()) {
            livres.enqueue(c.getNumero());
            c.setNaFila(true);
        }
    }

    private Consultorio obter(int numero) {
        if (numero < 1 || numero > TOTAL_CONSULTORIOS) {
            throw new NaoEncontradoException("Consultorio " + numero + " nao existe");
        }
        return consultorios[numero - 1];
    }

    private void registrar(String texto) {
        eventos.add(0, LocalDateTime.now().toLocalTime().withNano(0) + "  " + texto);
        if (eventos.size() > 30) {
            eventos.remove(eventos.size() - 1);
        }
    }

    public record SessaoEncerrada(Paciente paciente, int consultorio, LocalDateTime inicio) {
    }
}
