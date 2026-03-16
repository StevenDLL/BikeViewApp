package com.laughingalpaca.bikeviewapp;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.laughingalpaca.bikeviewapp.Model.Station;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationMapLayer extends MapLayer {
    private final Map<Station, Node> map = new HashMap<>();

    public StationMapLayer(List<Station> stationList, Pane pane, Label[] labels) {
        pane.setOpacity(0.9);
        for (Station station : stationList) {
            Circle circle = new Circle(5, Color.web("#625bb2"));
            circle.setStroke(Color.web("#2b2751"));
            circle.setStrokeWidth(3);

            circle.setOnMouseEntered(mouseEvent -> {
                this.setCursor(Cursor.HAND);
                circle.setFill(Color.web("#8f89d4"));
                circle.setRadius(7);
            });

            circle.setOnMouseExited(mouseEvent -> {
                this.setCursor(Cursor.DEFAULT);
                circle.setFill(Color.web("#625bb2"));
                circle.setRadius(5);
            });

            circle.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    pane.setVisible(true);
                    labels[0].setText(station.getStation_id());
                    labels[1].setText(station.getStation_name());
                    labels[2].setText(String.valueOf(station.getLocation().getLatitude()));
                    labels[3].setText(String.valueOf(station.getLocation().getLongitude()));
                    labels[4].setText(String.valueOf(station.getEstimated_bike_count()));
                }

            });

            Tooltip tooltip = new Tooltip(station.getStation_name());
            tooltip.setShowDelay(Duration.millis(100));
            tooltip.setStyle(
                    "-fx-background-color: #2b2751;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-family: Arial;" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-border-color: #625bb2;" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 6;" +
                            "-fx-background-radius: 6;" +
                            "-fx-padding: 6 10 6 10;"
            );

            Tooltip.install(circle, tooltip);

            map.put(station, circle);
            this.getChildren().add(circle);

        }

    }
    //TODO: Implement class constructor here that handles taking in a List<Station>
    // and creating a dot on the map for each individual station, also make it so
    // when a user clicks on said dot it will pop up with the information for that
    // Station, users should then be able to click on the displayed bike count to
    // see all bikes at the station with some information of the previous trip it took - Complete
    @Override
    protected void layoutLayer() {
        // Iterate through all markers and update their screen position
        map.forEach((station, node) -> {
            MapPoint point = station.getLocation();
            Point2D screenPoint = getMapPoint(point.getLatitude(), point.getLongitude());
            if (screenPoint != null) {
                node.setVisible(true);
                node.setTranslateX(screenPoint.getX());
                node.setTranslateY(screenPoint.getY());
            } else {
                node.setVisible(false);
            }
        });
    }
}


































