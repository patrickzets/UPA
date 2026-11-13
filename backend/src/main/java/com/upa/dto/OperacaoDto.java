package com.upa.dto;

import com.upa.modelo.Operacao;

import java.time.LocalDateTime;

public record OperacaoDto(String descricao, LocalDateTime hora) {

    public static OperacaoDto de(Operacao operacao) {
        return new OperacaoDto(operacao.getDescricao(), operacao.getHora());
    }
}
