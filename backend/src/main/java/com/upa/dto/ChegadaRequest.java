package com.upa.dto;

import com.upa.modelo.Cor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ChegadaRequest(
        @NotBlank(message = "CPF obrigatorio") String cpf,
        @NotNull(message = "Cor obrigatoria") Cor cor,
        @NotEmpty(message = "Informe ao menos um sintoma") List<Integer> sintomas) {
}
