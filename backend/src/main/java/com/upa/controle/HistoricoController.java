package com.upa.controle;

import com.upa.modelo.Atendimento;
import com.upa.servico.HistoricoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/historico")
public class HistoricoController {

    private final HistoricoService service;

    public HistoricoController(HistoricoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Atendimento> listar(@RequestParam(defaultValue = "inicio") String ordem) {
        return service.listar(ordem.equalsIgnoreCase("fim"));
    }

    @GetMapping("/primeiro")
    public Atendimento primeiro() {
        return service.irParaInicio();
    }

    @GetMapping("/ultimo")
    public Atendimento ultimo() {
        return service.irParaFim();
    }

    @GetMapping("/proximo")
    public Atendimento proximo() {
        return service.proximo();
    }

    @GetMapping("/anterior")
    public Atendimento anterior() {
        return service.anterior();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable int id) {
        service.remover(id);
    }
}
