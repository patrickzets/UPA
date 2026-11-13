package com.upa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoRequest(
        @NotBlank(message = "CPF obrigatorio") String cpf,
        @NotNull(message = "Data obrigatoria") LocalDate data,
        @NotNull(message = "Horario obrigatorio") LocalTime horario,
        @NotBlank(message = "Especialidade obrigatoria") String especialidade) {
}
