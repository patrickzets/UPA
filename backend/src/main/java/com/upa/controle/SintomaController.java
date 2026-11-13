package com.upa.controle;

import com.upa.modelo.Sintoma;
import com.upa.servico.SintomaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sintomas")
public class SintomaController {

    private final SintomaService service;

    public SintomaController(SintomaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Sintoma> listar() {
        return service.listar();
    }
}
