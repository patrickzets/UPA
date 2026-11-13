package com.upa.controle;

import com.upa.dto.ConfiguracaoRequest;
import com.upa.modelo.Configuracao;
import com.upa.servico.ConfiguracaoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configuracao")
public class ConfiguracaoController {

    private final ConfiguracaoService service;

    public ConfiguracaoController(ConfiguracaoService service) {
        this.service = service;
    }

    @GetMapping
    public Configuracao obter() {
        return service.obter();
    }

    @PutMapping
    public Configuracao atualizar(@Valid @RequestBody ConfiguracaoRequest dados) {
        return service.atualizar(dados);
    }
}
