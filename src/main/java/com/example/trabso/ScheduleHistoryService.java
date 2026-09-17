package com.example.trabso;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ScheduleHistoryService {
    private void goBack(Stage stage, Scene mainScene) {
        stage.setScene(mainScene);
        stage.show();
    }

    public void showScheduleHistory(Stage stage, List<String> history, Scene mainScene) {

        VBox vbox = new VBox();
        for(String h : history) {
            vbox.getChildren().add(new Label(h));
        }

        Button goBack = new Button("Voltar");
        goBack.setOnAction(event -> goBack(stage, mainScene));

        vbox.getChildren().add(goBack);

        Scene scene = new Scene(vbox, 600, 400);
        stage.setScene(scene);
        stage.show();
    }
}

