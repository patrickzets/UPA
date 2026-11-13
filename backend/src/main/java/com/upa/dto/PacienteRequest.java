package com.upa.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PacienteRequest(
        @NotBlank(message = "CPF obrigatorio") String cpf,
        @NotBlank(message = "Nome obrigatorio") String nome,
        @Min(value = 0, message = "Idade invalida") @Max(value = 130, message = "Idade invalida") int idade,
        @NotBlank(message = "Descricao obrigatoria") String descricao) {
}
