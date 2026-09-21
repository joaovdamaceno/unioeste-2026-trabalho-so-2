package com.example.trabso;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import java.util.Map;
import java.util.HashMap;

public class Simulador {
    public List<String> history;
    public List<List<ProcessElapsed>> processActivities;
    public Instant simulatedCurrentTime;
    public List<ProcessoSimulado> processesData;
    public Instant simulatedStartTime;
    public Long startTimeNano;
    public int contextSwitches;

    public void executar(TipoAlgoritmo algoritmo, List<Processo> processos) {
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

    private void admitirChegadas(
            Queue<Processo> futuros,
            Queue<ProcessoSimulado> filaProntos,
            int tempo
    ) {
        while (!futuros.isEmpty() && futuros.peek().getTempoChegada() <= tempo) {
            Processo processo = futuros.remove();
            history.add("Processo " + processo.getPid() + " está pronto");

            ProcessoSimulado novo = new ProcessoSimulado(processo);
            novo.setTempoChegada(simulatedStartTime.plusNanos((System.nanoTime()-startTimeNano)/1000000));
            filaProntos.add(novo);
        }
    }

    private void liberarBloqueados(
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
                history.add("Processo " + processo.getProcesso().getPid() + " está pronto");
                processo.setTempoTotalIO(processo.getTempoTotalIO() + (System.nanoTime() - processo.getInicioTempoIO())/100000);
                filaProntos.add(processo);
            }
        }
    }

    private void executarRoundRobin(List<Processo> processos) {
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

            contextSwitches++;

            ProcessoSimulado atual = filaProntos.remove();
            history.add("Processo " + atual.getProcesso().getPid() + " está executando");
            atual.setEstado(EstadoProcesso.EXECUTANDO);

            int inicio = tempo;
            int quantumUsado = 0;

            ProcessElapsed elapsed = new ProcessElapsed(simulatedCurrentTime);

            // inicio executando cpu
            long inicioTempoCpu = System.nanoTime();
            while (quantumUsado < quantum && atual.getTempoRestante() != 0)
            {
                if (atual.getPrimeiroTempoCpu()==null) {
                    atual.setPrimeiroTempoCpu(simulatedStartTime.plusNanos((System.nanoTime()-startTimeNano)/1000000));
                }

                atual.executarUnidade();
                tempo++;
                quantumUsado++;
                simulatedCurrentTime = simulatedCurrentTime.plus(16, ChronoUnit.SECONDS);

                admitirChegadas(futuros, filaProntos, tempo);
                liberarBloqueados(bloqueados, filaProntos, tempo);

                if (atual.deveSolicitarEs()) {
                    history.add("Processo " + atual.getProcesso().getPid() + " está bloqueado");
                    atual.bloquear(tempo);
                    bloqueados.add(atual);
                    atual.setInicioTempoIO(System.nanoTime());
                    break;
                }
            }

            // fim execução cpu
            atual.setTempoCpuTotal(atual.getTempoCpuTotal() + (System.nanoTime() - inicioTempoCpu)/100000);

            elapsed.end = simulatedCurrentTime;
            processActivities.get(atual.getProcesso().getIndex()).add(elapsed);

            System.out.println("PID: " + atual.getProcesso().getPid() + ", ");
            System.out.println("inicio: " + inicio + ", ");
            System.out.println("tempo: " + tempo + ", ");

            int tempoRestante = atual.getTempoRestante();
            System.out.println("tempo restante: " + tempoRestante + "\n");

            if (tempoRestante == 0) {
                atual.setTempoFim(simulatedStartTime.plusNanos((System.nanoTime()-startTimeNano)/1000000));
                history.add("Processo " + atual.getProcesso().getPid() + " foi finalizado");
                atual.setEstado(EstadoProcesso.FINALIZADO);
                processesData.add(atual);
            } else if (atual.getEstado() != EstadoProcesso.BLOQUEADO) {
                atual.setEstado(EstadoProcesso.PRONTO);
                filaProntos.add(atual);
            }
        }
    }
    private void adicionarNaFila(
            ProcessoSimulado processo,
            int fila,
            int tempo,
            Queue<ProcessoSimulado> fila1,
            Queue<ProcessoSimulado> fila2,
            Queue<ProcessoSimulado> fila3
    ) {
        switch (fila) {

            case 1:
                processo.setQuantumQueueUp(0);
                fila1.add(processo);
                break;

            case 2:
                processo.setQuantumQueueUp(
                        tempo + fila1.size() * 2
                );
                fila2.add(processo);
                break;

            case 3:
                processo.setQuantumQueueUp(
                        tempo + fila2.size() * 4
                );
                fila3.add(processo);
                break;

            default:
                throw new IllegalArgumentException(
                        "Fila sugerida inválida: " + fila
                );
        }
    }

    private void recebeFuturos(Queue<Processo> futuros, Queue<ProcessoSimulado> fila1, Queue<ProcessoSimulado> fila2, Queue<ProcessoSimulado> fila3, int tempo) {
        while (
                !futuros.isEmpty()
                        && futuros.peek().getTempoChegada() <= tempo
        ) {

            Processo processo = futuros.remove();

            history.add(
                    "Processo "
                            + processo.getPid()
                            + " está pronto"
            );

            ProcessoSimulado novo =
                    new ProcessoSimulado(processo);

            novo.setTempoChegada(
                    simulatedStartTime.plusNanos(
                            (System.nanoTime()
                                    - startTimeNano) / 1000000
                    )
            );

            adicionarNaFila(
                    novo,
                    processo.getFilaSugerida(),
                    tempo,
                    fila1,
                    fila2,
                    fila3
            );
        }
    }

    private void liberaBloqueados(Iterator<ProcessoSimulado> iterator, Queue<ProcessoSimulado> fila1, Queue<ProcessoSimulado> fila2, Queue<ProcessoSimulado> fila3, int tempo) {
        while (iterator.hasNext()) {

            ProcessoSimulado processo = iterator.next();

            if (processo.getDesbloqueioEm() <= tempo) {

                iterator.remove();

                processo.desbloquear();

                history.add(
                        "Processo "
                                + processo.getProcesso().getPid()
                                + " está pronto"
                );

                processo.setTempoTotalIO(
                        processo.getTempoTotalIO()
                                + (System.nanoTime()
                                - processo.getInicioTempoIO()) / 100000
                );

                adicionarNaFila(
                        processo,
                        processo.getProcesso().getFilaSugerida(),
                        tempo,
                        fila1,
                        fila2,
                        fila3
                );
            }
        }
    }

    private void executarMultiplasFilas(List<Processo> processos) {

        final int QUANTUM_FILA_1 = 2;
        final int QUANTUM_FILA_2 = 4;
        final int QUANTUM_FILA_3 = 8;

        int tempo = 0;

        Queue<ProcessoSimulado> fila1 = new ArrayDeque<>();
        Queue<ProcessoSimulado> fila2 = new ArrayDeque<>();
        Queue<ProcessoSimulado> fila3 = new ArrayDeque<>();

        List<ProcessoSimulado> bloqueados = new ArrayList<>();

        List<Processo> ordenados = new ArrayList<>(processos);
        ordenados.sort(Comparator.comparingInt(Processo::getTempoChegada));

        Queue<Processo> futuros = new ArrayDeque<>(ordenados);

        while (
                !fila1.isEmpty()
                        || !fila2.isEmpty()
                        || !fila3.isEmpty()
                        || !futuros.isEmpty()
                        || !bloqueados.isEmpty()
        ) {

            recebeFuturos(futuros, fila1, fila2, fila3, tempo);

            Iterator<ProcessoSimulado> iterator = bloqueados.iterator();
            liberaBloqueados(iterator, fila1, fila2, fila3, tempo);

            // Promove processos

            // Fila 2 -> Fila 1
            if (!fila2.isEmpty()) {

                ProcessoSimulado processo = fila2.peek();

                if (tempo >= processo.getQuantumQueueUp()) {

                    fila2.remove();

                    history.add(
                            "Processo "
                                    + processo.getProcesso().getPid()
                                    + " subiu da fila 2 para a fila 1"
                    );

                    processo.setQuantumQueueUp(0);

                    fila1.add(processo);
                }
            }

            // Fila 3 -> Fila 2
            if (!fila3.isEmpty()) {

                ProcessoSimulado processo = fila3.peek();

                if (tempo >= processo.getQuantumQueueUp()) {

                    fila3.remove();

                    history.add(
                            "Processo "
                                    + processo.getProcesso().getPid()
                                    + " subiu da fila 3 para a fila 2"
                    );

                    int quantumParaSubir =
                            fila1.size() * QUANTUM_FILA_1;

                    processo.setQuantumQueueUp(
                            tempo + quantumParaSubir
                    );

                    fila2.add(processo);
                }
            }

            if (fila1.isEmpty()
                    && fila2.isEmpty()
                    && fila3.isEmpty()) {
                tempo++;
                continue;
            }

            // Escolha de processo
            ProcessoSimulado atual;
            int quantum;

            if (!fila1.isEmpty()) {

                atual = fila1.remove();
                quantum = QUANTUM_FILA_1;

            } else if (!fila2.isEmpty()) {

                atual = fila2.remove();
                quantum = QUANTUM_FILA_2;

            } else {

                atual = fila3.remove();
                quantum = QUANTUM_FILA_3;
            }

            contextSwitches++;

            history.add(
                    "Processo "
                            + atual.getProcesso().getPid()
                            + " está executando"
            );

            atual.setEstado(EstadoProcesso.EXECUTANDO);

            int quantumUsado = 0;

            ProcessElapsed elapsed =
                    new ProcessElapsed(simulatedCurrentTime);

            // Executa na cpu
            long inicioTempoCpu = System.nanoTime();

            while (
                    quantumUsado < quantum
                            && atual.getTempoRestante() != 0
            ) {

                if (atual.getPrimeiroTempoCpu() == null) {

                    atual.setPrimeiroTempoCpu(
                            simulatedStartTime.plusNanos(
                                    (System.nanoTime()
                                            - startTimeNano) / 1000000
                            )
                    );
                }

                atual.executarUnidade();

                tempo++;
                quantumUsado++;

                simulatedCurrentTime =
                        simulatedCurrentTime.plus(
                                16,
                                ChronoUnit.SECONDS
                        );

                recebeFuturos(futuros, fila1, fila2, fila3, tempo);

                iterator = bloqueados.iterator();
                liberaBloqueados(iterator, fila1, fila2, fila3, tempo);

                if (atual.deveSolicitarEs()) {
                    history.add(
                            "Processo "
                                    + atual.getProcesso().getPid()
                                    + " está bloqueado"
                    );
                    atual.bloquear(tempo);
                    bloqueados.add(atual);

                    atual.setInicioTempoIO(
                            System.nanoTime()
                    );
                    break;
                }
            }

            // Terminou execução
            atual.setTempoCpuTotal(
                    atual.getTempoCpuTotal()
                            + (System.nanoTime()
                            - inicioTempoCpu) / 100000
            );

            elapsed.end = simulatedCurrentTime;

            processActivities
                    .get(atual.getProcesso().getIndex())
                    .add(elapsed);

            if (atual.getTempoRestante() == 0) {
                atual.setTempoFim(
                        simulatedStartTime.plusNanos(
                                (System.nanoTime()
                                        - startTimeNano) / 1000000
                        )
                );
                history.add(
                        "Processo "
                                + atual.getProcesso().getPid()
                                + " foi finalizado"
                );
                atual.setEstado(
                        EstadoProcesso.FINALIZADO
                );
                processesData.add(atual);
            }
            else if (
                    atual.getEstado()
                            != EstadoProcesso.BLOQUEADO
            ) {
                atual.setEstado(
                        EstadoProcesso.PRONTO
                );
                adicionarNaFila(
                        atual,
                        atual.getProcesso().getFilaSugerida(),
                        tempo,
                        fila1,
                        fila2,
                        fila3
                );
            }
        }
    }

    private void ajustarQuantumTipo(
            Map<String, Integer> quantumPorTipo,
            String tipo,
            int quantumMinimo,
            int quantumMaximo,
            int ajuste,
            int quantumConcedido,
            int quantumUsado
    ) {
        int quantumAnterior = quantumPorTipo.get(tipo);
        double percentualUsado = quantumUsado / (double) quantumConcedido;

        int quantumNovo = quantumAnterior;

        if (percentualUsado >= 1.0) {
            quantumNovo = Math.min(quantumMaximo, quantumAnterior + ajuste);
        } else if (percentualUsado < 0.5) {
            quantumNovo = Math.max(quantumMinimo, quantumAnterior - ajuste);
        }



        System.out.println(
                "Quantum tipo: " + tipo
                        + ", anterior: " + quantumAnterior
                        + ", novo: " + quantumNovo
                        + ", uso do quantum: " + String.format("%.1f%%", percentualUsado * 100)
        );
    }

    private void executarMetodoProposto(List<Processo> processos) {
        int QUANTUM_INICIAL = 5;
        int QUANTUM_MINIMO = 2;
        int QUANTUM_MAXIMO = 10;
        int AJUSTE = 1;

        Map<String, Integer> quantumPorTipo = new HashMap<>();
        quantumPorTipo.put("tempo_real", QUANTUM_INICIAL);
        quantumPorTipo.put("interativo", QUANTUM_INICIAL);
        quantumPorTipo.put("io_bound", QUANTUM_INICIAL);
        quantumPorTipo.put("misto", QUANTUM_INICIAL);
        quantumPorTipo.put("cpu_bound", QUANTUM_INICIAL);
        quantumPorTipo.put("batch", QUANTUM_INICIAL);

        int tempo = 0;

        Queue<ProcessoSimulado> filaProntos = new ArrayDeque<>();

        List<Processo> ordenados = new ArrayList<>(processos);
        ordenados.sort(
                Comparator.comparingInt(Processo::getTempoChegada)
                        .thenComparingInt(Processo::getPrioridade)
        );

        Queue<Processo> futuros = new ArrayDeque<>(ordenados);

        List<ProcessoSimulado> bloqueados = new ArrayList<>();

        while (!filaProntos.isEmpty() || !futuros.isEmpty() || !bloqueados.isEmpty()) {
            admitirChegadas(futuros, filaProntos, tempo);
            liberarBloqueados(bloqueados, filaProntos, tempo);

            if (filaProntos.isEmpty()) {
                tempo++;
                continue;
            }

            contextSwitches++;

            ProcessoSimulado atual = filaProntos.remove();
            history.add("Processo " + atual.getProcesso().getPid() + " está executando");
            atual.setEstado(EstadoProcesso.EXECUTANDO);

            String tipo = atual.getProcesso().getTipoProcesso();
            int quantum = quantumPorTipo.get(tipo);

            int inicio = tempo;
            int quantumUsado = 0;

            ProcessElapsed elapsed = new ProcessElapsed(simulatedCurrentTime);

            // inicio executando cpu
            long inicioTempoCpu = System.nanoTime();
            while (quantumUsado < quantum && atual.getTempoRestante() != 0)
            {
                if (atual.getPrimeiroTempoCpu()==null) {
                    atual.setPrimeiroTempoCpu(simulatedStartTime.plusNanos((System.nanoTime()-startTimeNano)/1000000));
                }

                atual.executarUnidade();
                tempo++;
                quantumUsado++;
                simulatedCurrentTime = simulatedCurrentTime.plus(16, ChronoUnit.SECONDS);

                admitirChegadas(futuros, filaProntos, tempo);
                liberarBloqueados(bloqueados, filaProntos, tempo);

                if (atual.deveSolicitarEs()) {
                    history.add("Processo " + atual.getProcesso().getPid() + " está bloqueado");
                    atual.bloquear(tempo);
                    bloqueados.add(atual);
                    atual.setInicioTempoIO(System.nanoTime());
                    break;
                }
            }

            // fim execução cpu
            atual.setTempoCpuTotal(atual.getTempoCpuTotal() + (System.nanoTime() - inicioTempoCpu)/100000);

            elapsed.end = simulatedCurrentTime;
            processActivities.get(atual.getProcesso().getIndex()).add(elapsed);

            System.out.println("PID: " + atual.getProcesso().getPid() + ", ");
            System.out.println("inicio: " + inicio + ", ");
            System.out.println("tempo: " + tempo + ", ");

            int tempoRestante = atual.getTempoRestante();
            System.out.println("tempo restante: " + tempoRestante + "\n");

            ajustarQuantumTipo(
                    quantumPorTipo,
                    tipo,
                    QUANTUM_MINIMO,
                    QUANTUM_MAXIMO,
                    AJUSTE,
                    quantum,
                    quantumUsado
            );

            if (tempoRestante == 0) {
                    atual.setTempoFim(simulatedStartTime.plusNanos((System.nanoTime()-startTimeNano)/1000000));
                history.add("Processo " + atual.getProcesso().getPid() + " foi finalizado");
                atual.setEstado(EstadoProcesso.FINALIZADO);
                processesData.add(atual);
            } else if (atual.getEstado() != EstadoProcesso.BLOQUEADO) {
                atual.setEstado(EstadoProcesso.PRONTO);
                filaProntos.add(atual);
            }
        }

        System.out.println("=== Quantum final por tipo (Método Proposto - RRQA) ===");
        for (String tipo : List.of("tempo_real", "interativo", "io_bound", "misto", "cpu_bound", "batch")) {
            System.out.println(tipo + ": " + quantumPorTipo.get(tipo));
        }
    }
}