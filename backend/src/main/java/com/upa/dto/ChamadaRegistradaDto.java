package com.upa.dto;

import com.upa.modelo.Cor;

import java.time.LocalDateTime;

public record ChamadaRegistradaDto(int id, String nome, String senha, Cor cor, int consultorio, LocalDateTime hora) {
}
