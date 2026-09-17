package com.example.trabso;

import java.util.List;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class Simulador {

    public static void executar(TipoAlgoritmo algoritmo, List<Processo> processos) {
        if (algoritmo == null) {
            throw new IllegalArgumentException("Escolha um algoritmo");
        }

        if (processos == null || processos.isEmpty()) {
            throw new IllegalArgumentException("Informe os processos");
        }

        switch (algoritmo) {
            case ROUND_ROBIN -> executarRoundRobin(processos);
            case MULTIPLAS_FILAS -> executarMultiplasFilas(processos);
            case METODO_PROPOSTO -> executarMetodoProposto(processos);
        }
    }

    private static void admitirChegadas(
            Queue<Processo> futuros,
            Queue<ProcessoSimulado> filaProntos,
            int tempo
    ) {
        while (!futuros.isEmpty() && futuros.peek().getTempoChegada() <= tempo) {
            Processo processo = futuros.remove();
            filaProntos.add(new ProcessoSimulado(processo));
        }
    }

    private static void liberarBloqueados(
            List<ProcessoSimulado> bloqueados,
            Queue<ProcessoSimulado> filaProntos,
            int tempo
    ) {
        Iterator<ProcessoSimulado> iterator = bloqueados.iterator();

        while (iterator.hasNext()) {
            ProcessoSimulado processo = iterator.next();

            if (processo.getDesbloqueioEm() <= tempo) {
                iterator.remove();
                processo.desbloquear();
                filaProntos.add(processo);
            }
        }
    }

    private static void executarRoundRobin(List<Processo> processos) {
        final int quantum = 4;
        int tempo = 0;

        Queue<ProcessoSimulado> filaProntos = new ArrayDeque<>();

        List<Processo> ordenados = new ArrayList<>(processos);
        ordenados.sort(Comparator.comparingInt(Processo::getTempoChegada));

        Queue<Processo> futuros = new ArrayDeque<>(ordenados);

        List<ProcessoSimulado> bloqueados = new ArrayList<>();

        while (!filaProntos.isEmpty() || !futuros.isEmpty() || !bloqueados.isEmpty()) {
            admitirChegadas(futuros, filaProntos, tempo);
            liberarBloqueados(bloqueados, filaProntos, tempo);

            if (filaProntos.isEmpty()) {
                tempo++;
                continue;
            }

            ProcessoSimulado atual = filaProntos.remove();
            atual.setEstado(EstadoProcesso.EXECUTANDO);

            int inicio = tempo;
            int quantumUsado = 0;

            while (quantumUsado < quantum && atual.getTempoRestante() != 0)
            {
                atual.executarUnidade();
                tempo++;
                quantumUsado++;

                admitirChegadas(futuros, filaProntos, tempo);
                liberarBloqueados(bloqueados, filaProntos, tempo);

                if (atual.deveSolicitarEs()) {
                    atual.bloquear(tempo);
                    bloqueados.add(atual);
                    break;
                }
            }

            System.out.println("PID: " + atual.getProcesso().getPid() + ", ");
            System.out.println("inicio: " + inicio + ", ");
            System.out.println("tempo: " + tempo + ", ");

            int tempoRestante = atual.getTempoRestante();
            System.out.println("tempo restante: " + tempoRestante + "\n");

            if (tempoRestante == 0) {
                atual.setEstado(EstadoProcesso.FINALIZADO);
            } else if (atual.getEstado() != EstadoProcesso.BLOQUEADO) {
                atual.setEstado(EstadoProcesso.PRONTO);
                filaProntos.add(atual);
            }
        }
    }

    private static void executarMultiplasFilas(List<Processo> processos) {
        System.out.println(
                "Teste de chamada: Múltiplas Filas recebeu "
                        + processos.size() + " processos"
        );
    }

    private static void executarMetodoProposto(List<Processo> processos) {
        System.out.println(
                "Teste de chamada: Método Proposto recebeu "
                        + processos.size() + " processos"
        );
    }
}