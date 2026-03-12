package com.laughingalpaca.bikeviewapp.Controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class AppLauncherController implements Initializable {

    public Pane appPane;
    public Pane appLoginPane;
    public Label lastUpdatedDateLabel;
    public Button launchMapButton;
    public Pane appIconHolder;
    public ImageView appIcon;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InitializeEventHandlers();
    }

    private void InitializeEventHandlers() {

    }

}
