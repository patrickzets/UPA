package com.upa.dto;

import com.upa.modelo.Consultorio;
import com.upa.modelo.Cor;
import com.upa.modelo.Paciente;

import java.time.Duration;
import java.time.LocalDateTime;

public record ConsultorioDto(int numero, boolean aberto, boolean ocupado, boolean naFila,
                             String paciente, String cpfPaciente, LocalDateTime inicioAtendimento,
                             Long reabreEmSegundos, Cor cor, String senha,
                             Long duracaoSegundos, Long restamSegundos) {

    public static ConsultorioDto de(Consultorio c) {
        Long restante = null;
        if (c.getReabreEm() != null) {
            restante = Math.max(0, c.getReabreEm().getEpochSecond() - java.time.Instant.now().getEpochSecond());
        }
        Paciente p = c.getPacienteAtual();
        Long duracao = null;
        Long restam = null;
        if (p != null && c.getInicioAtendimento() != null && c.getFimPrevisto() != null) {
            duracao = Duration.between(c.getInicioAtendimento(), c.getFimPrevisto()).toSeconds();
            restam = Math.max(0, Duration.between(LocalDateTime.now(), c.getFimPrevisto()).toSeconds());
        }
        return new ConsultorioDto(
                c.getNumero(),
                c.isAberto(),
                c.isOcupado(),
                c.isNaFila(),
                p != null ? p.getNome() : null,
                p != null ? p.getCpf() : null,
                c.getInicioAtendimento(),
                restante,
                p != null ? p.getCor() : null,
                p != null ? p.getSenha() : null,
                duracao,
                restam);
    }
}
