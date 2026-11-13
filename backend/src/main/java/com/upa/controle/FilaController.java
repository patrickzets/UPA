package com.upa.controle;

import com.upa.dto.ChamadaDto;
import com.upa.dto.ChegadaRequest;
import com.upa.dto.EstadoFilaDto;
import com.upa.modelo.Atendimento;
import com.upa.modelo.Paciente;
import com.upa.servico.AtendimentoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/fila")
public class FilaController {

    private final AtendimentoService service;

    public FilaController(AtendimentoService service) {
        this.service = service;
    }

    @GetMapping
    public EstadoFilaDto estado() {
        return service.estado();
    }

    @GetMapping("/proximo")
    public Paciente proximo() {
        return service.consultarProximo();
    }

    @PostMapping("/chegada")
    public Paciente chegada(@Valid @RequestBody ChegadaRequest dados) {
        return service.registrarChegada(dados);
    }

    @PostMapping("/triar")
    public Paciente triar() {
        return service.triarProximo();
    }

    @PostMapping("/triar-todos")
    public Map<String, Integer> triarTodos() {
        return Map.of("triados", service.triarTodos());
    }

    @PostMapping("/chamar")
    public ChamadaDto chamar() {
        return service.chamarProximo();
    }

    @PostMapping("/concluir/{consultorio}")
    public Atendimento concluir(@PathVariable int consultorio) {
        return service.concluir(consultorio);
    }
}
