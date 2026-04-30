package com.laughingalpaca.bikeviewapp;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;

import java.util.ArrayList;
import java.util.List;

public class BoundaryMapLayer extends MapLayer {
    private final GeoBoundary boundary;
    private final List<Polygon> polygons = new ArrayList<>();

    public BoundaryMapLayer(GeoBoundary boundary) {
        this.boundary = boundary;

        for (int i = 0; i < boundary.rings().size(); i++) {
            Polygon polygon = new Polygon();
            polygon.setStroke(Color.RED);
            polygon.setStrokeWidth(3);
            polygon.setFill(Color.color(1, 0, 0, 0.08));
            polygon.setStrokeLineJoin(StrokeLineJoin.ROUND);
            polygons.add(polygon);
            getChildren().add(polygon);
        }
    }

    @Override
    protected void layoutLayer() {
        for (int ringIndex = 0; ringIndex < boundary.rings().size(); ringIndex++) {
            List<MapPoint> ring = boundary.rings().get(ringIndex);
            Polygon polygon = polygons.get(ringIndex);
            polygon.getPoints().clear();

            for (MapPoint point : ring) {
                Point2D screenPoint = getMapPoint(point.getLatitude(), point.getLongitude());
                if (screenPoint != null) {
                    polygon.getPoints().add(screenPoint.getX());
                    polygon.getPoints().add(screenPoint.getY());
                }
            }

            polygon.setVisible(polygon.getPoints().size() >= 6);
        }
    }
}
