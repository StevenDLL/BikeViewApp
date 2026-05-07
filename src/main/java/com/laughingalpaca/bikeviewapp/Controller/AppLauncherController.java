package com.laughingalpaca.bikeviewapp.Controller;

import com.laughingalpaca.bikeviewapp.SceneManager;
import com.laughingalpaca.bikeviewapp.View.AppEntry;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.net.URI;
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
        launchMapButton.setOnMouseClicked(mouseEvent -> {
            SceneManager.getInstance().switchScene("/com/laughingalpaca/bikeviewapp/CitiBikeProjectView.fxml");
        });
    }

    public void githubClicked(MouseEvent mouseEvent) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI("https://github.com/StevenDLL/BikeViewApp"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
