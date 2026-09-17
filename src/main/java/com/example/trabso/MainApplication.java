package com.example.trabso;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(getClass().getResource("landing.fxml"));

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("landing.fxml")
        );

        Parent root = loader.load();

        Scene scene = new Scene(root, 600, 400 );
        stage.setScene(scene);
        stage.show();
    }
}

