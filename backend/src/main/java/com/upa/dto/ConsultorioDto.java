package com.upa.dto;

import com.upa.modelo.Consultorio;

import java.time.LocalDateTime;

public record ConsultorioDto(int numero, boolean aberto, boolean ocupado, boolean naFila,
                             String paciente, String cpfPaciente, LocalDateTime inicioAtendimento,
                             Long reabreEmSegundos) {

    public static ConsultorioDto de(Consultorio c) {
        Long restante = null;
        if (c.getReabreEm() != null) {
            restante = Math.max(0, c.getReabreEm().getEpochSecond() - java.time.Instant.now().getEpochSecond());
        }
        return new ConsultorioDto(
                c.getNumero(),
                c.isAberto(),
                c.isOcupado(),
                c.isNaFila(),
                c.isOcupado() ? c.getPacienteAtual().getNome() : null,
                c.isOcupado() ? c.getPacienteAtual().getCpf() : null,
                c.getInicioAtendimento(),
                restante);
    }
}
