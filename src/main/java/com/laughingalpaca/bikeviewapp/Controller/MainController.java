package com.laughingalpaca.bikeviewapp.Controller;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import com.laughingalpaca.bikeviewapp.DataHandler;
import com.laughingalpaca.bikeviewapp.Model.Station;
import com.laughingalpaca.bikeviewapp.StationMapLayer;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    //PANES
    public TabPane mainTabPane;
    public Tab mapViewTab;
    public AnchorPane mapViewPane;
    public StackPane mapPane;
    public Pane stationInfoPane;

    //LABELS
    public Label filterMapByLabel;
    public Label boroughLabel;
    public Label zipLabel;
    public Label stationLabel;
    public Label minBikeCountLabel;
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

    //CHOICES
    public ChoiceBox<String> boroughChoiceBox;
    public CheckBox zipCodeCheckBox;
    public CheckBox boroughCheckBox;
    public CheckBox stationCheckBox;
    public CheckBox bikeCountCheckBox;
    public ChoiceBox<String> stationChoiceBox;
    public Slider minBikeCountSlider;


    //TEXT FIELDS
    public TextArea infoTextArea;
    public TextField zipCodeTextField;


    //BUTTONS
    public Button showResultsButton;


    //INFO
    private final DataHandler dataHandler = DataHandler.getInstance();
    private List<Station> allStations = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeSlider();
        initializeEventHandlers();
        initializeFilterState();
        loadInitialData();
    }

    private void initializeSlider() {
        minBikeCountSlider.setMin(0);
        minBikeCountSlider.setMax(100);
        minBikeCountSlider.setMajorTickUnit(20);
        minBikeCountSlider.setMinorTickCount(3);
        minBikeCountSlider.setBlockIncrement(1);
        minBikeCountSlider.setValue(0);
    }

    private void loadInitialData() {
        allStations = dataHandler.getAllStations();
        initializeChoiceBoxes();
        updateSliderRange();
        initializeMapView(allStations);
        mapViewPane.setDisable(false);
        updateInfoPanel(allStations);
    }

    private void initializeEventHandlers() {

        zipCodeCheckBox.setDisable(true);
        zipLabel.setDisable(true);
        zipCodeTextField.setDisable(true);
        boroughCheckBox.setOnAction(event -> {
            boolean selected = boroughCheckBox.isSelected();
            boroughLabel.setDisable(!selected);
            boroughChoiceBox.setDisable(!selected);
            if (!selected) {
                boroughChoiceBox.getSelectionModel().clearSelection();
            }
        });
        stationCheckBox.setOnAction(event -> {
            boolean selected = stationCheckBox.isSelected();
            stationLabel.setDisable(!selected);
            stationChoiceBox.setDisable(!selected);
            if (!selected) {
                stationChoiceBox.getSelectionModel().clearSelection();
            }
        });
        bikeCountCheckBox.setOnAction(event -> {
            boolean selected = bikeCountCheckBox.isSelected();
            minBikeCountLabel.setDisable(!selected);
            minBikeCountSlider.setDisable(!selected);
            if (!selected) {
                minBikeCountSlider.setValue(minBikeCountSlider.getMin());
            }
        });
        showResultsButton.setOnAction(event -> applyFilters());
    }

    private void initializeFilterState() {
        zipCodeCheckBox.setSelected(false);
        boroughCheckBox.setSelected(false);
        stationCheckBox.setSelected(false);
        bikeCountCheckBox.setSelected(false);
        boroughLabel.setDisable(true);
        boroughChoiceBox.setDisable(true);
        stationLabel.setDisable(true);
        stationChoiceBox.setDisable(true);
        minBikeCountLabel.setDisable(true);
        minBikeCountSlider.setDisable(true);
    }

    private void initializeChoiceBoxes() {
        boroughChoiceBox.getItems().setAll(allStations.stream()
                .map(Station::getBorough)
                .filter(borough -> borough != null && !borough.isBlank())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .distinct()
                .toList());
        stationChoiceBox.getItems().setAll(allStations.stream()
                .map(Station::getStation_name)
                .filter(name -> name != null && !name.isBlank())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList());
    }

    private void updateSliderRange() {
        int maxBikeCount = allStations.stream()
                .mapToInt(Station::getEstimated_bike_count)
                .max()
                .orElse(100);
        minBikeCountSlider.setMax(Math.max(maxBikeCount, 10));
        minBikeCountSlider.setMajorTickUnit(Math.max(5, Math.ceil(maxBikeCount / 5.0)));
    }

    private void applyFilters() {
        List<Station> filteredStations = allStations.stream()
                .filter(this::matchesBorough)
                .filter(this::matchesStation)
                .filter(this::matchesBikeCount)
                .collect(Collectors.toList());
        initializeMapView(filteredStations);
        updateInfoPanel(filteredStations);
    }

    private boolean matchesBorough(Station station) {
        if (!boroughCheckBox.isSelected() || boroughChoiceBox.getValue() == null) {
            return true;
        }
        return boroughChoiceBox.getValue().equalsIgnoreCase(station.getBorough());
    }

    private boolean matchesStation(Station station) {
        if (!stationCheckBox.isSelected() || stationChoiceBox.getValue() == null) {
            return true;
        }
        return stationChoiceBox.getValue().equalsIgnoreCase(station.getStation_name());
    }

    private boolean matchesBikeCount(Station station) {
        if (!bikeCountCheckBox.isSelected()) {
            return true;
        }
        return station.getEstimated_bike_count() >= Math.round(minBikeCountSlider.getValue());
    }

    private void updateInfoPanel(List<Station> stations) {
        StringBuilder builder = new StringBuilder();
        builder.append("Source: ").append(dataHandler.getDatabaseDisplayName()).append('\n');
        builder.append("Project: ").append(dataHandler.getProjectDisplayName()).append('\n');
        builder.append("Status: ").append(dataHandler.getStatusMessage()).append('\n');
        builder.append("Last Updated: ").append(dataHandler.getLastUpdatedDisplay()).append("\n\n");
        builder.append("Stations Loaded: ").append(allStations.size()).append('\n');
        builder.append("Stations Shown: ").append(stations.size()).append('\n');
        builder.append("ZIP filtering is enabled in Layer 4.");
        infoTextArea.setText(builder.toString());
    }

    private void initializeMapView(List<Station> stationsList) {
        mapPane.getChildren().clear();
        stationInfoPane.setVisible(false);
        MapView mapView = new MapView();
        mapView.setCenter(resolveMapCenter(stationsList));
        mapView.setZoom(stationsList.isEmpty() ? 11 : 12);
        Label[] labels = {
                stationId,
                stationName,
                stationLatitude,
                stationLongitude,
                stationBikeCount,
        };
        if (!stationsList.isEmpty()) {
            StationMapLayer stationMapLayer = new StationMapLayer(stationsList, stationInfoPane, labels);
            mapView.addLayer(stationMapLayer);
        }
        mapPane.getChildren().add(mapView);
    }

    private MapPoint resolveMapCenter(List<Station> stationsList) {
        if (stationsList.isEmpty()) {
            return new MapPoint(40.776676, -73.971321);
        }
        double averageLatitude = stationsList.stream()
                .mapToDouble(station -> station.getLocation().getLatitude())
                .average()
                .orElse(40.776676);
        double averageLongitude = stationsList.stream()
                .mapToDouble(station -> station.getLocation().getLongitude())
                .average()
                .orElse(-73.971321);
        return new MapPoint(averageLatitude, averageLongitude);
    }
}