package com.upa.dto;

import com.upa.modelo.Paciente;

public record ChamadaDto(Paciente paciente, int consultorio) {
}
