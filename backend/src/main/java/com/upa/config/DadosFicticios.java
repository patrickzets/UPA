package com.upa.config;

import com.upa.dto.AgendamentoRequest;
import com.upa.dto.ChegadaRequest;
import com.upa.dto.PacienteRequest;
import com.upa.modelo.Cor;
import com.upa.servico.AgendamentoService;
import com.upa.servico.AtendimentoService;
import com.upa.servico.OperacoesService;
import com.upa.servico.PacienteService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@Order(1)
@ConditionalOnProperty(name = "upa.dados-ficticios", havingValue = "true")
public class DadosFicticios implements ApplicationRunner {

    private final PacienteService pacienteService;
    private final AtendimentoService atendimentoService;
    private final AgendamentoService agendamentoService;
    private final OperacoesService operacoesService;

    public DadosFicticios(PacienteService pacienteService, AtendimentoService atendimentoService,
                          AgendamentoService agendamentoService, OperacoesService operacoesService) {
        this.pacienteService = pacienteService;
        this.atendimentoService = atendimentoService;
        this.agendamentoService = agendamentoService;
        this.operacoesService = operacoesService;
    }

    @Override
    public void run(ApplicationArguments args) {
        cadastrar("11122233344", "Ana Souza", 34, "Queda de moto");
        cadastrar("22233344455", "Bruno Lima", 28, "Ferimento por arma de fogo");
        cadastrar("33344455566", "Carla Mendes", 52, "Dor forte no peito");
        cadastrar("44455566677", "Diego Alves", 19, "Torceu o tornozelo no futebol");
        cadastrar("55566677788", "Elaine Rocha", 67, "Febre e tosse ha tres dias");
        cadastrar("66677788899", "Fabio Nunes", 41, "Dor de garganta");
        cadastrar("77788899900", "Gabriela Torres", 8, "Crise de asma");
        cadastrar("88899900011", "Henrique Dias", 75, "Convulsao em casa");

        chegar("44455566677", Cor.VERDE, 17);
        chegar("11122233344", Cor.VERMELHO, 12);
        chegar("33344455566", Cor.AMARELO, 11);
        chegar("66677788899", Cor.VERDE, 19);
        chegar("22233344455", Cor.VERMELHO, 1);
        chegar("55566677788", Cor.AMARELO, 15, 20);
        chegar("77788899900", Cor.AMARELO, 13);

        for (int i = 0; i < 6; i++) {
            atendimentoService.triarProximo();
        }

        int primeiro = atendimentoService.chamarProximo().consultorio();
        int segundo = atendimentoService.chamarProximo().consultorio();
        atendimentoService.concluir(primeiro);
        atendimentoService.concluir(segundo);

        LocalDate hoje = LocalDate.now();
        agendar("55566677788", hoje.plusDays(5), LocalTime.of(14, 30), "Clinica geral");
        agendar("33344455566", hoje.plusDays(1), LocalTime.of(9, 0), "Cardiologia");
        agendar("44455566677", hoje.plusDays(3), LocalTime.of(10, 15), "Ortopedia");
        agendar("66677788899", hoje.plusDays(1), LocalTime.of(8, 30), "Otorrinolaringologia");

        operacoesService.limpar();
    }

    private void cadastrar(String cpf, String nome, int idade, String descricao) {
        pacienteService.cadastrar(new PacienteRequest(cpf, nome, idade, descricao));
    }

    private void chegar(String cpf, Cor cor, Integer... sintomas) {
        atendimentoService.registrarChegada(new ChegadaRequest(cpf, cor, List.of(sintomas)));
    }

    private void agendar(String cpf, LocalDate data, LocalTime horario, String especialidade) {
        agendamentoService.agendar(new AgendamentoRequest(cpf, data, horario, especialidade));
    }
}
