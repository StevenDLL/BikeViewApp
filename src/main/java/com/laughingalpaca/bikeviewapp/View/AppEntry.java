package com.laughingalpaca.bikeviewapp.View;

import com.laughingalpaca.bikeviewapp.DataHandler;
import com.laughingalpaca.bikeviewapp.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AppEntry extends Application {

    DataHandler appDataHandler;

    //TODO: Use this class to handle all data related things. Connect to the db here, query the db here,
    @Override
    public void init() throws Exception {
        appDataHandler = new DataHandler();
        super.init();
    }

    @Override
    public void start(Stage stage) throws IOException {

        stage.setTitle("Citi Bike Viewing App");
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNIFIED);
        stage.centerOnScreen();

        SceneManager.getInstance().setStage(stage);
        SceneManager.getInstance().switchScene("/com/laughingalpaca/bikeviewapp/AppLauncherView.fxml");
    }
}
