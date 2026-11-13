package com.upa.dto;

import jakarta.validation.constraints.Min;

public record ConfiguracaoRequest(
        @Min(value = 5, message = "O intervalo minimo e 5 segundos") int intervaloFechamentoSeg,
        @Min(value = 5, message = "A duracao minima e 5 segundos") int duracaoFechamentoSeg,
        boolean fechamentoAutomatico) {
}
