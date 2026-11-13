package com.upa.controle;

import com.upa.dto.ConsultorioDto;
import com.upa.dto.FilaCircularDto;
import com.upa.modelo.Consultorio;
import com.upa.servico.ConsultorioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/consultorios")
public class ConsultorioController {

    private final ConsultorioService service;

    public ConsultorioController(ConsultorioService service) {
        this.service = service;
    }

    @GetMapping
    public List<ConsultorioDto> listar() {
        List<ConsultorioDto> lista = new ArrayList<>();
        for (Consultorio c : service.listar()) {
            lista.add(ConsultorioDto.de(c));
        }
        return lista;
    }

    @GetMapping("/fila-circular")
    public FilaCircularDto filaCircular() {
        return service.estadoDaFila();
    }

    @GetMapping("/eventos")
    public List<String> eventos() {
        return service.getEventos();
    }

    @PostMapping("/{numero}/fechar")
    public void fechar(@PathVariable int numero) {
        service.fechar(numero, true);
    }

    @PostMapping("/{numero}/abrir")
    public void abrir(@PathVariable int numero) {
        service.abrir(numero);
    }
}
