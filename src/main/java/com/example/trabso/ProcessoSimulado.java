package com.example.trabso;

import java.time.Instant;
import java.util.Random;

public class ProcessoSimulado {
    private final Processo processo;
    private int tempoRestante;
    private EstadoProcesso estado = EstadoProcesso.PRONTO;
    private int desbloqueioEm = -1;
    private final Random sorteador;
    private Instant tempoChegada;
    private Instant primeiroTempoCpu = null;
    private Instant tempoFim;
    private long inicioTempoIO;
    private long tempoCpuTotal = 0;
    private long tempoTotalIO = 0;
    private int quantumQueueUp = 0;

    public ProcessoSimulado(Processo processo) {
        this.processo = processo;
        this.tempoRestante = processo.getTempoCpuTotal();
        this.sorteador = new Random(42L + processo.getPid().hashCode());
    }

    public Processo getProcesso() {
        return processo;
    }

    public int getTempoRestante() {
        return tempoRestante;
    }

    public EstadoProcesso getEstado() {
        return estado;
    }

    public void setEstado(EstadoProcesso estado) {
        this.estado = estado;
    }
    public int getQuantumQueueUp() {
        return quantumQueueUp;
    }

    public void setQuantumQueueUp(int quantumQueueUp) {
        this.quantumQueueUp = quantumQueueUp;
    }
    public int getDesbloqueioEm() {
        return desbloqueioEm;
    }
    public long getInicioTempoIO() {
        return inicioTempoIO;
    }

    public void setInicioTempoIO(long inicioTempoIO) {
        this.inicioTempoIO = inicioTempoIO;
    }
    public Instant getTempoChegada() {
        return tempoChegada;
    }

    public void setTempoChegada(Instant tempoChegada) {
        this.tempoChegada = tempoChegada;
    }

    public Instant getPrimeiroTempoCpu() {
        return primeiroTempoCpu;
    }

    public void setPrimeiroTempoCpu(Instant primeiroTempoCpu) {
        this.primeiroTempoCpu = primeiroTempoCpu;
    }

    public Instant getTempoFim() {
        return tempoFim;
    }

    public void setTempoFim(Instant tempoFim) {
        this.tempoFim = tempoFim;
    }

    public long getTempoCpuTotal() {
        return tempoCpuTotal;
    }

    public void setTempoCpuTotal(long tempoCpuTotal) {
        this.tempoCpuTotal = tempoCpuTotal;
    }

    public long getTempoTotalIO() {
        return tempoTotalIO;
    }

    public void setTempoTotalIO(long tempoTotalIO) {
        this.tempoTotalIO = tempoTotalIO;
    }

    public void executarUnidade() {
        if (tempoRestante <= 0) {
            throw new IllegalStateException("com.example.trabso.Processo já finalizado");
        }

        tempoRestante--;
    }

    public void bloquear(int tempoAtual)
    {
        estado = EstadoProcesso.BLOQUEADO;
        desbloqueioEm = tempoAtual + processo.getDuracaoEs();
    }

    public void desbloquear()
    {
        estado = EstadoProcesso.PRONTO;
        desbloqueioEm = -1;
    }

    public boolean deveSolicitarEs()
    {
        if (!getProcesso().isOperacaoEs()) return false;
        if (tempoRestante <= 0) return false;

        double numeroSorteado = sorteador.nextDouble();
        return numeroSorteado < getProcesso().getProbabilidadeEs();
    }
}