package com.upa.controle;

import com.upa.dto.OperacaoDto;
import com.upa.modelo.Operacao;
import com.upa.servico.OperacoesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/operacoes")
public class OperacoesController {

    private final OperacoesService service;

    public OperacoesController(OperacoesService service) {
        this.service = service;
    }

    @GetMapping
    public List<OperacaoDto> listar() {
        List<OperacaoDto> lista = new ArrayList<>();
        for (Operacao o : service.listar()) {
            lista.add(OperacaoDto.de(o));
        }
        return lista;
    }

    @PostMapping("/desfazer")
    public Map<String, String> desfazer() {
        return Map.of("mensagem", service.desfazer());
    }
}
