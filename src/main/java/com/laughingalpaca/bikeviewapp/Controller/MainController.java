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

    //TODO: Daniels todo, was having issues with github so i pasted his code in for him. --DONE
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

        // new Stations
        tempList.add(new Station("ST21", "W 4 St - Wash Sq", new MapPoint(40.7308, -74.0006), (int) (Math.random() * 301)));
        tempList.add(new Station("ST22", "86 St - Central Park", new MapPoint(40.7831, -73.9745), (int) (Math.random() * 301)));
        tempList.add(new Station("ST23", "Lexington Av/59 St", new MapPoint(40.7626, -73.9680), (int) (Math.random() * 301)));
        tempList.add(new Station("ST24", "Chelsea Market", new MapPoint(40.7420, -74.0048), (int) (Math.random() * 301)));
        tempList.add(new Station("ST25", "High Line - 23 St", new MapPoint(40.7481, -74.0042), (int) (Math.random() * 301)));
        tempList.add(new Station("ST26", "Washington Sq North", new MapPoint(40.7323, -73.9972), (int) (Math.random() * 301)));
        tempList.add(new Station("ST27", "East Village - 2nd Av", new MapPoint(40.7284, -73.9880), (int) (Math.random() * 301)));
        tempList.add(new Station("ST28", "Lower East Side - Delancey", new MapPoint(40.7186, -73.9881), (int) (Math.random() * 301)));
        tempList.add(new Station("ST29", "Williamsburg Bridge Plaza", new MapPoint(40.7107, -73.9602), (int) (Math.random() * 301)));
        tempList.add(new Station("ST30", "Bedford Av - L Train", new MapPoint(40.7161, -73.9584), (int) (Math.random() * 301)));
        tempList.add(new Station("ST31", "McCarren Park", new MapPoint(40.7209, -73.9548), (int) (Math.random() * 301)));
        tempList.add(new Station("ST32", "Greenpoint Av", new MapPoint(40.7301, -73.9543), (int) (Math.random() * 301)));
        tempList.add(new Station("ST33", "Long Island City - Court Sq", new MapPoint(40.7485, -73.9451), (int) (Math.random() * 301)));
        tempList.add(new Station("ST34", "Queens Plaza", new MapPoint(40.7490, -73.9372), (int) (Math.random() * 301)));
        tempList.add(new Station("ST35", "Sunnyside - 46 St", new MapPoint(40.7435, -73.9184), (int) (Math.random() * 301)));
        tempList.add(new Station("ST36", "Gantry Plaza State Park", new MapPoint(40.7454, -73.9587), (int) (Math.random() * 301)));
        tempList.add(new Station("ST37", "Brooklyn Navy Yard", new MapPoint(40.7012, -73.9721), (int) (Math.random() * 301)));
        tempList.add(new Station("ST38", "Fort Greene Park", new MapPoint(40.6917, -73.9757), (int) (Math.random() * 301)));
        tempList.add(new Station("ST39", "Clinton Hill - DeKalb Av", new MapPoint(40.6905, -73.9654), (int) (Math.random() * 301)));
        tempList.add(new Station("ST40", "Prospect Park West", new MapPoint(40.6662, -73.9787), (int) (Math.random() * 301)));
        tempList.add(new Station("ST41", "Park Slope - 7 Av", new MapPoint(40.6705, -73.9764), (int) (Math.random() * 301)));
        tempList.add(new Station("ST42", "Grand Army Plaza", new MapPoint(40.6738, -73.9703), (int) (Math.random() * 301)));
        tempList.add(new Station("ST43", "Crown Heights - Franklin Av", new MapPoint(40.6710, -73.9550), (int) (Math.random() * 301)));
        tempList.add(new Station("ST44", "Brooklyn Museum", new MapPoint(40.6712, -73.9631), (int) (Math.random() * 301)));
        tempList.add(new Station("ST45", "Bushwick - Myrtle-Wyckoff", new MapPoint(40.6998, -73.9115), (int) (Math.random() * 301)));
        tempList.add(new Station("ST46", "Jefferson St - L Train", new MapPoint(40.7066, -73.9231), (int) (Math.random() * 301)));
        tempList.add(new Station("ST47", "Upper East Side - 96 St", new MapPoint(40.7844, -73.9471), (int) (Math.random() * 301)));
        tempList.add(new Station("ST48", "Harlem - 125 St", new MapPoint(40.8077, -73.9454), (int) (Math.random() * 301)));
        tempList.add(new Station("ST49", "Morningside Heights", new MapPoint(40.8090, -73.9627), (int) (Math.random() * 301)));
        tempList.add(new Station("ST50", "Columbia University", new MapPoint(40.8075, -73.9626), (int) (Math.random() * 301)));
        tempList.add(new Station("ST51", "Washington Heights - 181 St", new MapPoint(40.8496, -73.9359), (int) (Math.random() * 301)));
        tempList.add(new Station("ST52", "Inwood - 207 St", new MapPoint(40.8673, -73.9213), (int) (Math.random() * 301)));
        tempList.add(new Station("ST53", "Riverside Park", new MapPoint(40.8002, -73.9718), (int) (Math.random() * 301)));
        tempList.add(new Station("ST54", "Lincoln Center", new MapPoint(40.7725, -73.9835), (int) (Math.random() * 301)));
        tempList.add(new Station("ST55", "Hell's Kitchen - 10 Av", new MapPoint(40.7644, -73.9920), (int) (Math.random() * 301)));
        tempList.add(new Station("ST56", "Hudson Yards", new MapPoint(40.7522, -74.0004), (int) (Math.random() * 301)));
        tempList.add(new Station("ST57", "Meatpacking District", new MapPoint(40.7410, -74.0076), (int) (Math.random() * 301)));
        tempList.add(new Station("ST58", "SoHo - Prince St", new MapPoint(40.7231, -73.9994), (int) (Math.random() * 301)));
        tempList.add(new Station("ST59", "Tribeca - Duane St", new MapPoint(40.7162, -74.0083), (int) (Math.random() * 301)));
        tempList.add(new Station("ST60", "Battery Park", new MapPoint(40.7033, -74.0170), (int) (Math.random() * 301)));
        tempList.add(new Station("ST61", "South St Seaport", new MapPoint(40.7071, -74.0011), (int) (Math.random() * 301)));
        tempList.add(new Station("ST62", "Roosevelt Island Tram", new MapPoint(40.7611, -73.9642), (int) (Math.random() * 301)));
        tempList.add(new Station("ST63", "Astoria Park", new MapPoint(40.7795, -73.9220), (int) (Math.random() * 301)));
        tempList.add(new Station("ST64", "Steinway St", new MapPoint(40.7596, -73.9198), (int) (Math.random() * 301)));
        tempList.add(new Station("ST65", "Queensboro Plaza", new MapPoint(40.7503, -73.9402), (int) (Math.random() * 301)));
        tempList.add(new Station("ST66", "Woodside - 61 St", new MapPoint(40.7458, -73.9029), (int) (Math.random() * 301)));
        tempList.add(new Station("ST67", "Forest Hills - 71 Av", new MapPoint(40.7216, -73.8444), (int) (Math.random() * 301)));
        tempList.add(new Station("ST68", "Kew Gardens", new MapPoint(40.7061, -73.8300), (int) (Math.random() * 301)));
        tempList.add(new Station("ST69", "Red Hook - Coffey Park", new MapPoint(40.6761, -74.0114), (int) (Math.random() * 301)));
        tempList.add(new Station("ST70", "Carroll Gardens", new MapPoint(40.6812, -73.9953), (int) (Math.random() * 301)));
        tempList.add(new Station("ST71", "Gowanus - 3rd Av", new MapPoint(40.6734, -73.9890), (int) (Math.random() * 301)));
        tempList.add(new Station("ST72", "Sunset Park", new MapPoint(40.6455, -74.0124), (int) (Math.random() * 301)));
        tempList.add(new Station("ST73", "Bay Ridge - 95 St", new MapPoint(40.6161, -74.0309), (int) (Math.random() * 301)));
        tempList.add(new Station("ST74", "Bensonhurst", new MapPoint(40.6120, -73.9995), (int) (Math.random() * 301)));
        tempList.add(new Station("ST75", "Williamsburg - Marcy Av", new MapPoint(40.7083, -73.9577), (int) (Math.random() * 301)));
    }

    }


    //TODO: Implement displaying a map using https://github.com/gluonhq/maps -- DONE
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
