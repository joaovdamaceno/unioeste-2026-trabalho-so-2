import java.util.Random;

public class ProcessoSimulado {
    private final Processo processo;
    private int tempoRestante;
    private EstadoProcesso estado = EstadoProcesso.PRONTO;
    private int desbloqueioEm = -1;
    private final Random sorteador;

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

    public int getDesbloqueioEm() {
        return desbloqueioEm;
    }

    public void executarUnidade() {
        if (tempoRestante <= 0) {
            throw new IllegalStateException("Processo já finalizado");
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