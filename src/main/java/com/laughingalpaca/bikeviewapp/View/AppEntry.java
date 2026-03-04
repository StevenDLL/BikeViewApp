package com.laughingalpaca.bikeviewapp.View;

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
        FXMLLoader fxmlLoader = new FXMLLoader(AppEntry.class.getResource("/com/laughingalpaca/bikeviewapp/CitiBikeProjectView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        scene.getStylesheets().add(String.valueOf(getClass().getResource("/com/laughingalpaca/bikeviewapp/stylesheet.css")));
        stage.setTitle("Citi Bike Viewing App");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNIFIED);
        stage.show();
    }

}
