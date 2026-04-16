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
}
