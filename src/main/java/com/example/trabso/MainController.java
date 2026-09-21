package com.example.trabso;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainController {
    @FXML
    private Label welcomeText;
    @FXML
    private TextField textfield;

    @FXML
    protected void showResults(ActionEvent event) throws IOException {
        String path = textfield.getText();
        Path caminho = Path.of(
            path
        );


        List<Processo> processos = LeitorProcessosCsv.lerArquivo(caminho);
        Simulador simulador = new Simulador();
        simulador.simulatedCurrentTime = Instant.now();
        simulador.history = new ArrayList<>();
        simulador.processActivities = new ArrayList<>(processos.size());
        simulador.processesData = new ArrayList<>();
        for(int i = 0; i < processos.size(); i++) simulador.processActivities.add(new ArrayList<>());

        simulador.startTimeNano = System.nanoTime();
        simulador.simulatedStartTime = Instant.now();
        simulador.contextSwitches = 0;

        Button button = (Button) event.getSource();
        String algoritmo = button.getText();
        if (Objects.equals(algoritmo, "Round Robin")) {
            simulador.executar(TipoAlgoritmo.ROUND_ROBIN, processos);
        }
        else if (algoritmo.equals("Múltiplas Filas")) {
            simulador.executar(TipoAlgoritmo.MULTIPLAS_FILAS, processos);
        }
        else simulador.executar(TipoAlgoritmo.METODO_PROPOSTO, processos);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("resultpage.fxml"));
        Parent root = loader.load();

        ResultPageController resultPageController = loader.getController();
        resultPageController.simulador = simulador;

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);

        resultPageController.mainScene = scene;
        stage.show();
    }
}

