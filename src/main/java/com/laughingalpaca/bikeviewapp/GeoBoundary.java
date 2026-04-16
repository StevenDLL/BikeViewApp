package com.laughingalpaca.bikeviewapp;

import com.gluonhq.maps.MapPoint;
import java.util.List;

public record GeoBoundary(
        String id,
        String label,
        List<List<MapPoint>> rings,
        double minLatitude,
        double maxLatitude,
        double minLongitude,
        double maxLongitude
) {
    public MapPoint getCenter() {
        return new MapPoint(
                (minLatitude + maxLatitude) / 2.0,
                (minLongitude + maxLongitude) / 2.0
        );
    }

    public double getLatitudeSpan() {
        return Math.max(0.0, maxLatitude - minLatitude);
    }

    public double getLongitudeSpan() {
        return Math.max(0.0, maxLongitude - minLongitude);
    }
}
