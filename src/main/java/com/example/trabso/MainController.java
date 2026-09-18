package com.example.trabso;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick(ActionEvent event) {
        Button button = (Button) event.getSource();

        welcomeText.setText(button.getText());
    }

    @FXML
    protected void showResults(ActionEvent event) throws IOException {
        Path caminho = Path.of(
            "C:/Users/Thiago/Downloads/processos_entrada_correlacionados (1).csv"
        );

        Simulador simulador = new Simulador();
        List<Processo> processos = LeitorProcessosCsv.lerArquivo(caminho);
        simulador.executar(TipoAlgoritmo.ROUND_ROBIN, processos);

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

