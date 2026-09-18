package com.example.trabso;

public class Processo {
    private final String pid;
    private final String nomeProcesso;
    private final int tempoChegada;
    private final int tempoCpuTotal;
    private final int prioridade;
    private final String tipoProcesso;
    private final boolean operacaoEs;
    private final double probabilidadeEs;
    private final double mediaEs;
    private final int duracaoEs;
    private final int filaSugerida;
    private final int quantumSugerido;
    private final String descricao;
    private final int index;

    public Processo(String pid, String nomeProcesso, int tempoChegada, int tempoCpuTotal, int prioridade, String tipoProcesso, boolean operacaoEs, double probabilidadeEs, double mediaEs, int duracaoEs, int filaSugerida, int quantumSugerido, String descricao, int index) {
        this.pid = pid;
        this.nomeProcesso = nomeProcesso;
        this.tempoChegada = tempoChegada;
        this.tempoCpuTotal = tempoCpuTotal;
        this.prioridade = prioridade;
        this.tipoProcesso = tipoProcesso;
        this.operacaoEs = operacaoEs;
        this.probabilidadeEs = probabilidadeEs;
        this.mediaEs = mediaEs;
        this.duracaoEs = duracaoEs;
        this.filaSugerida = filaSugerida;
        this.quantumSugerido = quantumSugerido;
        this.descricao = descricao;
        this.index = index;
    }

    public String getPid() {
        return pid;
    }

    public String getNomeProcesso() {
        return nomeProcesso;
    }

    public int getTempoChegada() {
        return tempoChegada;
    }

    public int getTempoCpuTotal() {
        return tempoCpuTotal;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public String getTipoProcesso() {
        return tipoProcesso;
    }

    public boolean isOperacaoEs() {
        return operacaoEs;
    }

    public double getProbabilidadeEs() {
        return probabilidadeEs;
    }

    public double getMediaEs() {
        return mediaEs;
    }

    public int getDuracaoEs() {
        return duracaoEs;
    }

    public int getFilaSugerida() {
        return filaSugerida;
    }

    public int getQuantumSugerido() {
        return quantumSugerido;
    }

    public String getDescricao() {
        return descricao;
    }
    public int getIndex() {
        return index;
    }
}
