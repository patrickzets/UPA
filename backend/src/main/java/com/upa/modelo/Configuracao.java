package com.upa.modelo;

public class Configuracao {

    private int intervaloFechamentoSeg = 60;
    private int duracaoFechamentoSeg = 30;
    private boolean fechamentoAutomatico = true;

    public int getIntervaloFechamentoSeg() {
        return intervaloFechamentoSeg;
    }

    public void setIntervaloFechamentoSeg(int intervaloFechamentoSeg) {
        this.intervaloFechamentoSeg = intervaloFechamentoSeg;
    }

    public int getDuracaoFechamentoSeg() {
        return duracaoFechamentoSeg;
    }

    public void setDuracaoFechamentoSeg(int duracaoFechamentoSeg) {
        this.duracaoFechamentoSeg = duracaoFechamentoSeg;
    }

    public boolean isFechamentoAutomatico() {
        return fechamentoAutomatico;
    }

    public void setFechamentoAutomatico(boolean fechamentoAutomatico) {
        this.fechamentoAutomatico = fechamentoAutomatico;
    }
}
