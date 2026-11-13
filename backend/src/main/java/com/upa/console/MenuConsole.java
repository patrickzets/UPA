package com.upa.console;

import com.upa.dto.AgendamentoRequest;
import com.upa.dto.ChegadaRequest;
import com.upa.dto.PacienteRequest;
import com.upa.modelo.Agendamento;
import com.upa.modelo.Atendimento;
import com.upa.modelo.Consultorio;
import com.upa.modelo.Cor;
import com.upa.modelo.Paciente;
import com.upa.modelo.Sintoma;
import com.upa.servico.AgendamentoService;
import com.upa.servico.AtendimentoService;
import com.upa.servico.ConsultorioService;
import com.upa.servico.HistoricoService;
import com.upa.servico.OperacoesService;
import com.upa.servico.PacienteService;
import com.upa.servico.SintomaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
@Profile("console")
@Order(2)
public class MenuConsole implements CommandLineRunner {

    private final PacienteService pacienteService;
    private final AtendimentoService atendimentoService;
    private final AgendamentoService agendamentoService;
    private final HistoricoService historicoService;
    private final ConsultorioService consultorioService;
    private final OperacoesService operacoesService;
    private final SintomaService sintomaService;
    private final Scanner teclado = new Scanner(System.in);

    public MenuConsole(PacienteService pacienteService, AtendimentoService atendimentoService,
                       AgendamentoService agendamentoService, HistoricoService historicoService,
                       ConsultorioService consultorioService, OperacoesService operacoesService,
                       SintomaService sintomaService) {
        this.pacienteService = pacienteService;
        this.atendimentoService = atendimentoService;
        this.agendamentoService = agendamentoService;
        this.historicoService = historicoService;
        this.consultorioService = consultorioService;
        this.operacoesService = operacoesService;
        this.sintomaService = sintomaService;
    }

    @Override
    public void run(String... args) {
        int opcao = -1;
        while (opcao != 0) {
            mostrarMenu();
            opcao = lerInteiro("Opcao: ");
            try {
                executar(opcao);
            } catch (RuntimeException e) {
                System.out.println(">> " + e.getMessage());
            }
        }
        System.out.println("Encerrando.");
        System.exit(0);
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("===== UPA =====");
        System.out.println("1  Cadastrar paciente");
        System.out.println("2  Buscar paciente por CPF");
        System.out.println("3  Listar pacientes");
        System.out.println("4  Remover paciente");
        System.out.println("5  Registrar chegada (recepcao)");
        System.out.println("6  Triar proximo da recepcao");
        System.out.println("7  Ver proximo a ser chamado");
        System.out.println("8  Chamar proximo");
        System.out.println("9  Concluir atendimento");
        System.out.println("10 Ver filas e atendidos");
        System.out.println("11 Agendar consulta");
        System.out.println("12 Listar agenda");
        System.out.println("13 Remover agendamento");
        System.out.println("14 Historico (inicio ao fim)");
        System.out.println("15 Historico (fim ao inicio)");
        System.out.println("16 Navegar historico (p = proximo, a = anterior)");
        System.out.println("17 Ver consultorios e fila circular");
        System.out.println("18 Fechar consultorio");
        System.out.println("19 Abrir consultorio");
        System.out.println("20 Ultimas operacoes");
        System.out.println("21 Desfazer ultima operacao");
        System.out.println("0  Sair");
    }

    private void executar(int opcao) {
        switch (opcao) {
            case 1 -> cadastrar();
            case 2 -> System.out.println(descrever(pacienteService.buscar(lerTexto("CPF: "))));
            case 3 -> pacienteService.listar().forEach(p -> System.out.println(descrever(p)));
            case 4 -> {
                pacienteService.remover(lerTexto("CPF: "));
                System.out.println("Paciente removido.");
            }
            case 5 -> chegada();
            case 6 -> System.out.println("Triado: " + descrever(atendimentoService.triarProximo()));
            case 7 -> System.out.println("Proximo: " + descrever(atendimentoService.consultarProximo()));
            case 8 -> {
                var chamada = atendimentoService.chamarProximo();
                System.out.println(chamada.paciente().getNome() + " foi para o consultorio " + chamada.consultorio());
            }
            case 9 -> {
                Atendimento a = atendimentoService.concluir(lerInteiro("Consultorio: "));
                System.out.println("Atendimento de " + a.getNomePaciente() + " concluido.");
            }
            case 10 -> mostrarFilas();
            case 11 -> agendar();
            case 12 -> agendamentoService.listar().forEach(this::mostrarAgendamento);
            case 13 -> {
                agendamentoService.remover(lerInteiro("Id do agendamento: "));
                System.out.println("Agendamento removido.");
            }
            case 14 -> historicoService.listar(false).forEach(this::mostrarAtendimento);
            case 15 -> historicoService.listar(true).forEach(this::mostrarAtendimento);
            case 16 -> navegarHistorico();
            case 17 -> mostrarConsultorios();
            case 18 -> {
                consultorioService.fechar(lerInteiro("Consultorio: "), true);
                System.out.println("Consultorio fechado.");
            }
            case 19 -> {
                consultorioService.abrir(lerInteiro("Consultorio: "));
                System.out.println("Consultorio aberto.");
            }
            case 20 -> operacoesService.listar().forEach(o -> System.out.println(o.getHora().toLocalTime().withNano(0)
                    + "  " + o.getDescricao()));
            case 21 -> System.out.println(operacoesService.desfazer());
            case 0 -> { }
            default -> System.out.println("Opcao invalida.");
        }
    }

    private void cadastrar() {
        String cpf = lerTexto("CPF: ");
        String nome = lerTexto("Nome: ");
        int idade = lerInteiro("Idade: ");
        String descricao = lerTexto("Solicitacao: ");
        Paciente p = pacienteService.cadastrar(new PacienteRequest(cpf, nome, idade, descricao));
        System.out.println("Cadastrado: " + p.getNome());
    }

    private void chegada() {
        String cpf = lerTexto("CPF: ");
        Cor cor = Cor.valueOf(lerTexto("Cor (VERMELHO, AMARELO, VERDE): ").toUpperCase());
        for (Sintoma s : sintomaService.listar()) {
            System.out.println(s.id() + " " + s.nome() + " (" + s.pontos() + " pts)");
        }
        List<Integer> ids = new ArrayList<>();
        for (String parte : lerTexto("Ids dos sintomas separados por virgula: ").split(",")) {
            ids.add(Integer.parseInt(parte.trim()));
        }
        Paciente p = atendimentoService.registrarChegada(new ChegadaRequest(cpf, cor, ids));
        System.out.println(p.getNome() + " esta na recepcao com " + p.getPontuacao() + " pontos.");
    }

    private void agendar() {
        String cpf = lerTexto("CPF: ");
        LocalDate data = LocalDate.parse(lerTexto("Data (aaaa-mm-dd): "));
        LocalTime horario = LocalTime.parse(lerTexto("Horario (hh:mm): "));
        String especialidade = lerTexto("Especialidade: ");
        Agendamento a = agendamentoService.agendar(new AgendamentoRequest(cpf, data, horario, especialidade));
        System.out.println("Agendamento " + a.getId() + " criado.");
    }

    private void mostrarFilas() {
        var estado = atendimentoService.estado();
        System.out.println("Recepcao:");
        estado.recepcao().forEach(p -> System.out.println("  " + descrever(p)));
        System.out.println("Aguardando (ordem de chamada):");
        estado.aguardando().forEach(p -> System.out.println("  " + descrever(p)));
        System.out.println("Em atendimento:");
        estado.emAtendimento().forEach(p -> System.out.println("  " + descrever(p)));
        System.out.println("Ja atendidos:");
        historicoService.listar(false).forEach(this::mostrarAtendimento);
    }

    private void navegarHistorico() {
        String comando = lerTexto("p = proximo, a = anterior: ");
        Atendimento a = comando.equalsIgnoreCase("a") ? historicoService.anterior() : historicoService.proximo();
        mostrarAtendimento(a);
    }

    private void mostrarConsultorios() {
        for (Consultorio c : consultorioService.listar()) {
            String situacao = !c.isAberto() ? "FECHADO" : c.isOcupado() ? "atendendo " + c.getPacienteAtual() : "livre";
            System.out.println("Consultorio " + c.getNumero() + ": " + situacao);
        }
        var fila = consultorioService.estadoDaFila();
        System.out.println("Vetor da fila circular: " + fila.vetor() + " inicio=" + fila.inicio() + " fim=" + fila.fim());
    }

    private String descrever(Paciente p) {
        String situacao = p.getCor() == null ? "" : " | " + p.getCor() + " " + p.getPontuacao() + " pts";
        return p.getCpf() + " | " + p.getNome() + " | " + p.getIdade() + " anos | " + p.getStatus() + situacao;
    }

    private void mostrarAgendamento(Agendamento a) {
        System.out.println(a.getId() + " | " + a.getData() + " " + a.getHorario() + " | " + a.getEspecialidade()
                + " | CPF " + a.getCpf());
    }

    private void mostrarAtendimento(Atendimento a) {
        System.out.println(a.getId() + " | " + a.getNomePaciente() + " | " + a.getCor() + " " + a.getPontuacao()
                + " pts | consultorio " + a.getConsultorio() + " | " + a.getInicio().toLocalTime().withNano(0)
                + " ate " + a.getFim().toLocalTime().withNano(0));
    }

    private String lerTexto(String pergunta) {
        System.out.print(pergunta);
        return teclado.nextLine().trim();
    }

    private int lerInteiro(String pergunta) {
        try {
            return Integer.parseInt(lerTexto(pergunta));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
