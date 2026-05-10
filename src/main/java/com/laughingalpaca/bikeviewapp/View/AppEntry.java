package com.laughingalpaca.bikeviewapp.View;

import com.laughingalpaca.bikeviewapp.DataHandler;
import com.laughingalpaca.bikeviewapp.FirestoreSeeder;
import com.laughingalpaca.bikeviewapp.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AppEntry extends Application {


    @Override
    public void init() throws Exception {
        DataHandler.getInstance().refreshConnectionStatus();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Citi Bike Viewing App");
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNIFIED);
        stage.centerOnScreen();
        SceneManager.getInstance().setStage(stage);
        SceneManager.getInstance().switchScene("/com/laughingalpaca/bikeviewapp/AppLauncherView.fxml");
    }
}
