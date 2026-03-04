package com.laughingalpaca.bikeviewapp.Controller;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import com.laughingalpaca.bikeviewapp.StationMapLayer;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

    //TODO: Here we will handle initializing all data, event handling, etc
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        InitializeEventHandlers();
        InitializeChoiceBoxes();

        //TODO: We will pass the actual stationList that we receive from the database, for now we pass a blank list
        InitializeMapView(new ArrayList<>());
    }

    private void InitializeEventHandlers() {

        //Handle Checkbox Logic for ZIPCODE
        zipCodeCheckBox.setOnAction(event -> {
            if (zipCodeCheckBox.isSelected()) {
                zipLabel.setDisable(false);
                zipCodeTextField.setDisable(false);
            } else {
                zipLabel.setDisable(true);
                zipCodeTextField.setDisable(true);
                zipCodeTextField.clear();
            }
        });

        //Handle Checkbox Logic for BOROUGH
        boroughCheckBox.setOnAction(event -> {
            if (boroughCheckBox.isSelected()) {
                boroughLabel.setDisable(false);
                boroughChoiceBox.setDisable(false);
            } else {
                boroughLabel.setDisable(true);
                boroughChoiceBox.setDisable(true);
                boroughChoiceBox.getSelectionModel().clearSelection();
            }
        });

        //Handle Checkbox Logic for STATION
        stationCheckBox.setOnAction(event -> {
            if (stationCheckBox.isSelected()) {
                stationLabel.setDisable(false);
                stationChoiceBox.setDisable(false);
            } else {
                stationLabel.setDisable(true);
                stationChoiceBox.setDisable(true);
                stationChoiceBox.getSelectionModel().clearSelection();
            }
        });

        //Handle Checkbox Logic for BIKE COUNT
        bikeCountCheckBox.setOnAction(event -> {
            if (bikeCountCheckBox.isSelected()) {
                minBikeCountLabel.setDisable(false);
                minBikeCountSlider.setDisable(false);
            } else {
                minBikeCountLabel.setDisable(true);
                minBikeCountSlider.setDisable(true);
                minBikeCountSlider.setValue(minBikeCountSlider.getMin());
            }
        });


        showResultsButton.setOnAction(e -> {
            System.out.println(
                    "Information Provided: " + "\n"
                            + "Zipcode: " + zipCodeTextField.getText() + "\n"
                            + "Borough: " + boroughChoiceBox.getValue() + "\n"
                            + "Station: " + stationChoiceBox.getValue() + "\n"
                            + "Min Bike Count: " + minBikeCountSlider.getValue()

            );
        });
    }

    //TODO: Here we will handle initializing the choice box options, pass all possible boroughs and all possible stations
    private void InitializeChoiceBoxes() {

    }

    //TODO: Implement displaying a map using https://github.com/gluonhq/maps
    private void InitializeMapView(List<Object> stationsList) {
        MapView mapView = new MapView();
        MapPoint newYorkPoint = new MapPoint(40.776676, -73.971321);
        mapView.setCenter(newYorkPoint);
        mapView.setZoom(12);
        StationMapLayer stationMapLayer = new StationMapLayer();
        mapView.addLayer(stationMapLayer);
        mapPane.getChildren().add(mapView);
    }

}
