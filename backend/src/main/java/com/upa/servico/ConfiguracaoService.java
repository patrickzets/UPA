package com.upa.servico;

import com.upa.dto.ConfiguracaoRequest;
import com.upa.modelo.Configuracao;
import org.springframework.stereotype.Service;

@Service
public class ConfiguracaoService {

    private final Configuracao configuracao = new Configuracao();

    public synchronized Configuracao obter() {
        return configuracao;
    }

    public synchronized Configuracao atualizar(ConfiguracaoRequest dados) {
        configuracao.setIntervaloFechamentoSeg(dados.intervaloFechamentoSeg());
        configuracao.setDuracaoFechamentoSeg(dados.duracaoFechamentoSeg());
        configuracao.setFechamentoAutomatico(dados.fechamentoAutomatico());
        return configuracao;
    }
}
