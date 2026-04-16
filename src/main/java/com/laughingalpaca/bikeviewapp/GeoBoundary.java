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
    public boolean contains(MapPoint point) {
        boolean inside = false;
        for (List<MapPoint> ring : rings) {
            inside ^= isInsideRing(point, ring);
        }
        return inside;
    }

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

    private boolean isInsideRing(MapPoint point, List<MapPoint> ring) {
        boolean inside = false;
        double x = point.getLongitude();
        double y = point.getLatitude();

        for (int i = 0, j = ring.size() - 1; i < ring.size(); j = i++) {
            MapPoint current = ring.get(i);
            MapPoint previous = ring.get(j);

            double xi = current.getLongitude();
            double yi = current.getLatitude();
            double xj = previous.getLongitude();
            double yj = previous.getLatitude();

            boolean intersects = ((yi > y) != (yj > y))
                    && (x < (xj - xi) * (y - yi)
                    / ((yj - yi) == 0 ? Double.MIN_NORMAL : (yj - yi)) + xi);

            if (intersects) {
                inside = !inside;
            }
        }

        return inside;
    }
}
