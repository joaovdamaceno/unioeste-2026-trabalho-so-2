package com.example.trabso;

import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.MutableActivityBase;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.timeline.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class GanttService {
    public static class ProcessActivity extends MutableActivityBase<Object> {
        public ProcessActivity(
                String name,
                Instant start,
                Instant end
        ) {
            setName(name);
            setStartTime(start);
            setEndTime(end);
        }
    }

    public static class ProcessRow extends Row<ProcessRow, ProcessRow, ProcessActivity> {
        public ProcessRow(String name) {
            super(name);
        }
    }

    public void goBack(Stage stage, Scene mainScene) throws IOException {

        stage.setScene(mainScene);
        stage.show();
    }

    public void showChart(Stage stage, Scene mainScene) throws IOException { // Teste
        // Criação gantt
        GanttChart<ProcessRow> gantt = new GanttChart<>(new ProcessRow("Processos"));

        Layer schedulingLayer = new Layer("Escalonamento");

        gantt.getLayers().add(schedulingLayer);

        // Cria linhas para os processos e coloca as atividades
        ProcessRow p1 = new ProcessRow("Processo 1");
        p1.addActivity(schedulingLayer,
                new ProcessActivity("Processo 1 ",
                        Instant.now(),
                        Instant.now().plus(7, ChronoUnit.HOURS)));

        p1.addActivity(schedulingLayer,
                new ProcessActivity("Processo 2 ",
                        Instant.now().plus(9, ChronoUnit.HOURS),
                        Instant.now().plus(23, ChronoUnit.HOURS)));

        gantt.getRoot().getChildren().setAll(p1);

        Timeline timeline = gantt.getTimeline();

        timeline.showTemporalUnit(
                ChronoUnit.HOURS,
                20
        );

        GraphicsBase<ProcessRow> graphicsBase = gantt.getGraphics();

        graphicsBase.setActivityRenderer(
                ProcessActivity.class,
                GanttLayout.class,
                new ActivityBarRenderer<>(
                        graphicsBase,
                        "Process Renderer"
                )
        );

        graphicsBase.showAllActivities();

        // Botões para navegar
        Button home = new Button("Voltar");
        home.setOnAction(event -> {
            try {
                goBack(stage, mainScene);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Junta os componentes
        HBox buttonsRow = new HBox(2, home);
        VBox main = new VBox(gantt, buttonsRow);

        // Cria a cena

        Scene scene = new Scene(main,
                600, 400);
        stage.setScene(scene);
        stage.show();
    }
}

