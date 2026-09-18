package com.example.trabso;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResultPageController {

    public Simulador simulador;
    public Scene mainScene;

    @FXML
    public void goBackLandingPage(ActionEvent event) throws IOException {
        Parent root = (new FXMLLoader(getClass().getResource("landing.fxml")).load());;

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    @FXML
    public void showChart(ActionEvent event) throws IOException {
        GanttService ganttService = new GanttService();
        ganttService.showChart((Stage) ((Node)event.getSource()).getScene().getWindow(), mainScene);
    }

    @FXML
    public void showScheduleHistory(ActionEvent event) {
        ScheduleHistoryService scheduleHistoryService = new ScheduleHistoryService();
        scheduleHistoryService.showScheduleHistory((Stage) ((Node) event.getSource()).getScene().getWindow(), simulador.history, mainScene);
    }
}
