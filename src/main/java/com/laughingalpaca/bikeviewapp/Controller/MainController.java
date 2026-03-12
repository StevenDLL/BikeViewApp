package com.laughingalpaca.bikeviewapp.Controller;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import com.laughingalpaca.bikeviewapp.Model.Station;
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

    //TODO: Daniels todo, was having issues with github so i pasted his code in for him.
    private void FillMapList(List<Station> tempList) {
        tempList.add(new Station("ST01", "Times Square - 42 St", new MapPoint(40.7552, -73.9871), (int) (Math.random() * 301)));
        tempList.add(new Station("ST02", "Grand Central - 42 St", new MapPoint(40.7517, -73.9768), (int) (Math.random() * 301)));
        tempList.add(new Station("ST03", "34 St - Penn Station", new MapPoint(40.7505, -73.9936), (int) (Math.random() * 301)));
        tempList.add(new Station("ST04", "14 St - Union Square", new MapPoint(40.7346, -73.9903), (int) (Math.random() * 301)));
        tempList.add(new Station("ST05", "World Trade Center", new MapPoint(40.7126, -74.0099), (int) (Math.random() * 301)));
        tempList.add(new Station("ST06", "Atlantic Av - Barclays Ctr", new MapPoint(40.6844, -73.9785), (int) (Math.random() * 301)));
        tempList.add(new Station("ST07", "Fulton St", new MapPoint(40.7103, -74.0065), (int) (Math.random() * 301)));
        tempList.add(new Station("ST08", "59 St - Columbus Circle", new MapPoint(40.7682, -73.9819), (int) (Math.random() * 301)));
        tempList.add(new Station("ST09", "Coney Island - Stillwell Av", new MapPoint(40.5774, -73.9818), (int) (Math.random() * 301)));
        tempList.add(new Station("ST10", "Jackson Hts - Roosevelt Av", new MapPoint(40.7466, -73.8913), (int) (Math.random() * 301)));
        tempList.add(new Station("ST11", "Flushing - Main St", new MapPoint(40.7596, -73.8300), (int) (Math.random() * 301)));
        tempList.add(new Station("ST12", "161 St - Yankee Stadium", new MapPoint(40.8279, -73.9256), (int) (Math.random() * 301)));
        tempList.add(new Station("ST13", "St George Terminal", new MapPoint(40.6437, -74.0736), (int) (Math.random() * 301)));
        tempList.add(new Station("ST14", "Canal St", new MapPoint(40.7188, -74.0017), (int) (Math.random() * 301)));
        tempList.add(new Station("ST15", "Jay St - MetroTech", new MapPoint(40.6923, -73.9873), (int) (Math.random() * 301)));
        tempList.add(new Station("ST16", "Astoria - Ditmars Blvd", new MapPoint(40.7750, -73.9120), (int) (Math.random() * 301)));
        tempList.add(new Station("ST17", "Christopher St - Sheridan Sq", new MapPoint(40.7331, -74.0030), (int) (Math.random() * 301)));
        tempList.add(new Station("ST18", "72 St (Upper West Side)", new MapPoint(40.7784, -73.9819), (int) (Math.random() * 301)));
        tempList.add(new Station("ST19", "Fordham Rd", new MapPoint(40.8625, -73.8977), (int) (Math.random() * 301)));
        tempList.add(new Station("ST20", "Wall St", new MapPoint(40.7075, -74.0113), (int) (Math.random() * 301)));
    }

    //TODO: Implement displaying a map using https://github.com/gluonhq/maps - Done
    private void InitializeMapView(List<Station> stationsList) {
        MapView mapView = new MapView();
        MapPoint newYorkPoint = new MapPoint(40.776676, -73.971321);
        mapView.setCenter(newYorkPoint);
        mapView.setZoom(12);

        FillMapList(stationsList);
        Label[] labels = {
                stationId,
                stationName,
                stationLatitude,
                stationLongitude,
                stationBikeCount,
        };
        StationMapLayer stationMapLayer = new StationMapLayer(stationsList, stationInfoPane, labels);
        mapView.addLayer(stationMapLayer);
        mapPane.getChildren().add(mapView);
    }

}
