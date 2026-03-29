package com.laughingalpaca.bikeviewapp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private static SceneManager instance;
    private Stage primaryStage;
    private final Map<String, Scene> listOfScenes = new HashMap<>(3);

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void switchScene(String fxmlPath) {
        try {

            Scene scene = listOfScenes.get(fxmlPath);

            if (scene == null) {
                var resource = getClass().getResource(fxmlPath);
                if (resource == null) {
                    throw new RuntimeException("FXML file not found: " + fxmlPath);
                }
                Parent root = FXMLLoader.load(resource);
                scene = new Scene(root);
                listOfScenes.put(fxmlPath, scene);
            }

            scene.getStylesheets().add(String.valueOf(getClass().getResource("/com/laughingalpaca/bikeviewapp/stylesheet.css")));
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
