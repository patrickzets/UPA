package com.upa.servico;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AtendimentoScheduler {

    private final AtendimentoService atendimentoService;

    public AtendimentoScheduler(AtendimentoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    @Scheduled(fixedDelay = 1000)
    public void executar() {
        atendimentoService.processarAtendimentosAutomaticos();
    }
}