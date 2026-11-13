package com.upa.dto;

import com.upa.modelo.Paciente;

import java.util.List;

public record EstadoFilaDto(List<Paciente> recepcao, List<Paciente> aguardando, Paciente proximo,
                            List<Paciente> emAtendimento) {
}
