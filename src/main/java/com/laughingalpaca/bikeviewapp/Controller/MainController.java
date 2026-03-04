package com.laughingalpaca.bikeviewapp.Controller;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    //PANES RELATED
    public TabPane mainTabPane;
    public Tab mapViewTab;
    public AnchorPane mapViewPane;
    public StackPane mapPane;
    public Tab settingsTab;
    public Pane stationInfoPane;


    //LABELS
    public Label filterMapByLabel;
    public Label boroughLabel;
    public Label zipLabel;
    public Label stationLabel;
    public Label minBikeCountLabel;
    public Label databaseIpLabel;
    public Label stationIdLabel;
    public Label stationNameLabel;
    public Label stationLatitudeLabel;
    public Label stationLongitudeLabel;
    public Label stationBikeCountLabel;
    public Label stationId;
    public Label stationName;
    public Label stationLatitude;
    public Label stationLongitude;
    public Label stationBikeCount;


    //CHOICES RELATED
    public ChoiceBox<String> boroughChoiceBox;
    public CheckBox zipCodeCheckBox;
    public CheckBox boroughCheckBox;
    public CheckBox stationCheckBox;
    public CheckBox bikeCountCheckBox;
    public TextField zipCodeTextField;
    public ChoiceBox<String> stationChoiceBox;
    public Slider minBikeCountSlider;
    public TextArea infoTextArea;
    public TextField databaseNameTextField;
    public TextField databaseIpTextField;


    //BUTTONS
    public Button showResultsButton;
    public Button refreshDatabaseStatusButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
