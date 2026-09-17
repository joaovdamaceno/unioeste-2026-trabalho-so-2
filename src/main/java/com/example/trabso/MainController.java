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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resultpage.fxml"));
        Parent root = loader.load();

        List<String> hs = new ArrayList<>();
        hs.add("processo 1 executa");
        hs.add("processo 1 bloqueia");
        ResultPageController resultPageController = loader.getController();
        resultPageController.teste = hs;

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);

        resultPageController.mainScene = scene;
        stage.show();
    }
}

