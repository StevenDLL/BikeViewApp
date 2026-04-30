package com.laughingalpaca.bikeviewapp.Controller;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import com.laughingalpaca.bikeviewapp.BoundaryMapLayer;
import com.laughingalpaca.bikeviewapp.DataHandler;
import com.laughingalpaca.bikeviewapp.GeoBoundary;
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
    private static final List<String> NYC_BOROUGHS = List.of(
            "Bronx",
            "Brooklyn",
            "Manhattan",
            "Queens",
            "Staten Island"
    );

    public TabPane mainTabPane;
    public Tab mapViewTab;
    public AnchorPane mapViewPane;
    public StackPane mapPane;
    public Tab settingsTab;
    public Pane stationInfoPane;

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

    public Button showResultsButton;
    public Button refreshDatabaseStatusButton;

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
        refreshDatabaseStatus();
        initializeChoiceBoxes();
        updateSliderRange();
        initializeMapView(allStations, null);
        mapViewPane.setDisable(false);
        updateInfoPanel(allStations, null);
    }

    private void initializeEventHandlers() {
        zipCodeCheckBox.setOnAction(event -> {
            boolean selected = zipCodeCheckBox.isSelected();
            zipLabel.setDisable(!selected);
            zipCodeTextField.setDisable(!selected);
            if (!selected) {
                zipCodeTextField.clear();
            }
        });

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
        refreshDatabaseStatusButton.setOnAction(event -> {
            allStations = dataHandler.getAllStations();
            refreshDatabaseStatus();
            initializeChoiceBoxes();
            updateSliderRange();
            initializeMapView(allStations, null);
            updateInfoPanel(allStations, null);
        });
    }

    private void initializeFilterState() {
        zipCodeCheckBox.setSelected(false);
        boroughCheckBox.setSelected(false);
        stationCheckBox.setSelected(false);
        bikeCountCheckBox.setSelected(false);
        zipLabel.setDisable(true);
        zipCodeTextField.setDisable(true);
        zipCodeTextField.clear();
        boroughLabel.setDisable(true);
        boroughChoiceBox.setDisable(true);
        boroughChoiceBox.getSelectionModel().clearSelection();
        stationLabel.setDisable(true);
        stationChoiceBox.setDisable(true);
        stationChoiceBox.getSelectionModel().clearSelection();
        minBikeCountLabel.setDisable(true);
        minBikeCountSlider.setDisable(true);
        minBikeCountSlider.setValue(minBikeCountSlider.getMin());
    }

    private void initializeChoiceBoxes() {
        List<String> boroughs = new ArrayList<>(NYC_BOROUGHS);
        allStations.stream()
                .map(Station::getBorough)
                .filter(borough -> borough != null && !borough.isBlank())
                .filter(borough -> !boroughs.contains(borough))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .forEach(boroughs::add);
        boroughChoiceBox.getItems().setAll(boroughs);
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
        GeoBoundary zipBoundary = null;
        if (hasActiveZipFilter()) {
            String requestedZip = zipCodeTextField.getText() == null ? "" : zipCodeTextField.getText().trim();
            zipBoundary = dataHandler.getZipBoundary(requestedZip).orElse(null);
        }
        final GeoBoundary selectedZipBoundary = zipBoundary;
        List<Station> filteredStations = allStations.stream()
                .filter(station -> matchesZipCode(station, selectedZipBoundary))
                .filter(this::matchesBorough)
                .filter(this::matchesStation)
                .filter(this::matchesBikeCount)
                .collect(Collectors.toList());
        initializeMapView(filteredStations, selectedZipBoundary);
        updateInfoPanel(filteredStations, selectedZipBoundary);
    }

    private boolean matchesZipCode(Station station, GeoBoundary zipBoundary) {
        if (!hasActiveZipFilter()) {
            return true;
        }
        String requestedZip = zipCodeTextField.getText() == null ? "" : zipCodeTextField.getText().trim();
        return !requestedZip.isBlank() && zipBoundary != null && dataHandler.isStationInsideBoundary(station, zipBoundary);
    }

    private boolean matchesBorough(Station station) {
        if (!hasActiveBoroughFilter()) {
            return true;
        }
        String borough = boroughChoiceBox.getValue();
        return borough.equalsIgnoreCase(station.getBorough());
    }

    private boolean matchesStation(Station station) {
        if (!hasActiveStationFilter()) {
            return true;
        }
        String stationNameValue = stationChoiceBox.getValue();
        return stationNameValue.equalsIgnoreCase(station.getStation_name());
    }

    private boolean matchesBikeCount(Station station) {
        if (!bikeCountCheckBox.isSelected()) {
            return true;
        }
        return station.getEstimated_bike_count() >= Math.round(minBikeCountSlider.getValue());
    }
    private void refreshDatabaseStatus() {
        databaseNameTextField.setText(dataHandler.getDatabaseDisplayName());
        databaseIpTextField.setText(dataHandler.getDatabaseHostDisplay());
    }

    private void updateInfoPanel(List<Station> stations, GeoBoundary zipBoundary) {
        StringBuilder builder = new StringBuilder();
        builder.append("Source: ").append(dataHandler.getDatabaseDisplayName()).append('\n');
        builder.append("Project: ").append(dataHandler.getProjectDisplayName()).append('\n');
        builder.append("Status: ").append(dataHandler.getStatusMessage()).append('\n');
        builder.append("Last Updated: ").append(dataHandler.getLastUpdatedDisplay()).append("\n\n");
        builder.append("Stations Loaded: ").append(allStations.size()).append('\n');
        builder.append("Stations Shown: ").append(stations.size()).append('\n');
        builder.append("Filters: ").append(buildFilterSummary(zipBoundary)).append('\n');
        if (stations.isEmpty()) {
            builder.append("\nNo stations match the current filters.");
        } else {
            builder.append("\nShowing ").append(stations.size()).append(" station");
            if (stations.size() != 1) {
                builder.append('s');
            }
            builder.append('.');
        }
        if (zipCodeCheckBox.isSelected()) {
            builder.append("\nZip Search: ")
                    .append(zipCodeTextField.getText() == null ? "" : zipCodeTextField.getText().trim());
            if (zipBoundary == null) {
                builder.append(" (boundary not found)");
            } else {
                builder.append(" (boundary loaded)");
            }
        }
        infoTextArea.setText(builder.toString());
    }

    private boolean hasActiveZipFilter() {
        return zipCodeCheckBox.isSelected()
                && zipCodeTextField.getText() != null
                && !zipCodeTextField.getText().trim().isBlank();
    }
    private boolean hasActiveBoroughFilter() {
        return boroughCheckBox.isSelected()
                && boroughChoiceBox.getValue() != null
                && !boroughChoiceBox.getValue().isBlank();
    }
    private boolean hasActiveStationFilter() {
        return stationCheckBox.isSelected()
                && stationChoiceBox.getValue() != null
                && !stationChoiceBox.getValue().isBlank();
    }

    private String buildFilterSummary(GeoBoundary zipBoundary) {
        List<String> activeFilters = new ArrayList<>();
        if (hasActiveZipFilter()) {
            String zip = zipCodeTextField.getText().trim();
            activeFilters.add(zipBoundary == null ? "ZIP " + zip + " (not found)" : "ZIP " + zip);
        }
        if (hasActiveBoroughFilter()) {
            activeFilters.add("Borough " + boroughChoiceBox.getValue());
        }
        if (hasActiveStationFilter()) {
            activeFilters.add("Station " + stationChoiceBox.getValue());
        }
        if (bikeCountCheckBox.isSelected()) {
            activeFilters.add("Min Bikes " + Math.round(minBikeCountSlider.getValue()));
        }
        if (activeFilters.isEmpty()) {
            return "All stations";
        }
        return String.join(", ", activeFilters);
    }

    private void initializeMapView(List<Station> stationsList, GeoBoundary zipBoundary) {
        mapPane.getChildren().clear();
        stationInfoPane.setVisible(false);

        MapView mapView = new MapView();
        mapView.setCenter(resolveMapCenter(stationsList, zipBoundary));
        mapView.setZoom(resolveZoomLevel(stationsList, zipBoundary));

        Label[] labels = {
                stationId,
                stationName,
                stationLatitude,
                stationLongitude,
                stationBikeCount,
        };

        if (zipBoundary != null) {
            mapView.addLayer(new BoundaryMapLayer(zipBoundary));
        }
        if (!stationsList.isEmpty()) {
            StationMapLayer stationMapLayer = new StationMapLayer(stationsList, stationInfoPane, labels);
            mapView.addLayer(stationMapLayer);
        }

        mapPane.getChildren().add(mapView);
    }

    private MapPoint resolveMapCenter(List<Station> stationsList, GeoBoundary zipBoundary) {
        if (zipBoundary != null) {
            return zipBoundary.getCenter();
        }
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

    private int resolveZoomLevel(List<Station> stationsList, GeoBoundary zipBoundary) {
        if (zipBoundary != null) {
            double maxSpan = Math.max(zipBoundary.getLatitudeSpan(), zipBoundary.getLongitudeSpan());
            if (maxSpan > 0.25) {
                return 11;
            }
            if (maxSpan > 0.12) {
                return 12;
            }
            if (maxSpan > 0.06) {
                return 13;
            }
            if (maxSpan > 0.03) {
                return 14;
            }
            if (maxSpan > 0.015) {
                return 15;
            }
            return 16;
        }
        return stationsList.isEmpty() ? 11 : 12;
    }
}
