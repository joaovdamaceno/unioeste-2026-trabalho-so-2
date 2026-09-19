package com.example.trabso;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.Instant;
import java.util.List;


public class StatisticsService {
    private void goBack(Stage stage, Scene mainScene) {
        stage.setScene(mainScene);
        stage.show();
    }

    public void showStatistics(Stage stage,
                               List<ProcessoSimulado> data,
                               Scene mainScene) {

        VBox container = new VBox(10);
        container.setPadding(new Insets(15));

        long somaTempoEspera = 0;
        long somaTempoRetorno = 0;
        long somaTempoResposta = 0;

        for (ProcessoSimulado processo : data) {

            // Informações mostradas ao expandir o processo
            VBox detalhes = new VBox(5);

            detalhes.getChildren().addAll(
                    new Label(
                            "Tipo de processo: "
                                    + processo.getProcesso().getTipoProcesso()
                    ),
                    new Label(
                            "Tempo de chegada: "
                                    + processo.getTempoChegada()
                    ),
                    new Label(
                            "Primeiro tempo em CPU: "
                                    + processo.getPrimeiroTempoCpu()
                    ),
                    new Label(
                            "Tempo de fim: "
                                    + processo.getTempoFim()
                    ),
                    new Label(
                            "Tempo de CPU total: "
                                    + processo.getTempoCpuTotal()
                                    + " ms"
                    ),
                    new Label(
                            "Tempo total de I/O: "
                                    + processo.getTempoTotalIO()
                                    + " ms"
                    )
            );

            // Hambúrguer do processo
            TitledPane processoPane = new TitledPane(
                    processo.getProcesso().getPid(),
                    detalhes
            );

            processoPane.setExpanded(false);

            container.getChildren().add(processoPane);

            /*
             * Métricas:
             *
             * retorno = conclusão - chegada
             * espera  = retorno - CPU - I/O
             * resposta = primeiro CPU - chegada
             */

            Instant chegada = processo.getTempoChegada();
            Instant conclusao = processo.getTempoFim();
            Instant primeiroCpu = processo.getPrimeiroTempoCpu();

            long tempoRetorno =
                    Duration.between(chegada, conclusao).toNanos();

            long tempoResposta =
                    Duration.between(chegada, primeiroCpu).toNanos();

            long tempoEspera =
                    tempoRetorno
                            - processo.getTempoCpuTotal()
                            - processo.getTempoTotalIO();

            // Soma para calcular as médias
            somaTempoEspera += tempoEspera;
            somaTempoRetorno += tempoRetorno;
            somaTempoResposta += tempoResposta;

            // Métricas individuais
            container.getChildren().addAll(
                    new Label(
                            "Tempo de retorno: "
                                    + tempoRetorno
                                    + " ms"
                    ),
                    new Label(
                            "Tempo de espera: "
                                    + tempoEspera
                                    + " ms"
                    ),
                    new Label(
                            "Tempo de resposta: "
                                    + tempoResposta
                                    + " ms"
                    )
            );
        }

        // Médias
        if (!data.isEmpty()) {

            int quantidadeProcessos = data.size();

            double tempoMedioEspera =
                    (double) somaTempoEspera / quantidadeProcessos;

            double tempoMedioRetorno =
                    (double) somaTempoRetorno / quantidadeProcessos;

            double tempoMedioResposta =
                    (double) somaTempoResposta / quantidadeProcessos;

            container.getChildren().add(
                    new Separator()
            );

            container.getChildren().addAll(
                    new Label(
                            String.format(
                                    "Tempo médio de espera: %.2f ms",
                                    tempoMedioEspera
                            )
                    ),
                    new Label(
                            String.format(
                                    "Tempo médio de retorno: %.2f ms",
                                    tempoMedioRetorno
                            )
                    ),
                    new Label(
                            String.format(
                                    "Tempo médio de resposta: %.2f ms",
                                    tempoMedioResposta
                            )
                    )
            );
        }

        // Botão voltar
        Button backButton = new Button("Voltar");

        backButton.setOnAction(
                event -> goBack(stage, mainScene)
        );

        container.getChildren().add(backButton);

        // ScrollPane
        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);

        // Nova cena
        Scene statisticsScene = new Scene(
                scrollPane,
                800,
                600
        );

        stage.setScene(statisticsScene);
        stage.show();
    }

}
