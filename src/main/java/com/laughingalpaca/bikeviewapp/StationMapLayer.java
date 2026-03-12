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
            Circle circle = new Circle(8, Color.ORANGERED);
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(3);

            circle.setOnMouseEntered(mouseEvent -> {
                this.setCursor(Cursor.HAND);
            });

            circle.setOnMouseExited(mouseEvent -> {
                this.setCursor(Cursor.DEFAULT);
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
            tooltip.setStyle("-fx-font-size: 14");

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
