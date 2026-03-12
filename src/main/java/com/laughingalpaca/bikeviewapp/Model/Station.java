package com.laughingalpaca.bikeviewapp.Model;

import com.gluonhq.maps.MapPoint;

public class Station {
    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public MapPoint getLocation() {
        return location;
    }

    public void setLocation(MapPoint location) {
        this.location = location;
    }

    public int getEstimated_bike_count() {
        return estimated_bike_count;
    }

    public void setEstimated_bike_count(int estimated_bike_count) {
        this.estimated_bike_count = estimated_bike_count;
    }

    private String station_id;
    private String station_name;
    private MapPoint location;
    private int estimated_bike_count;

    public Station(String station_id, String station_name, MapPoint location, int estimated_bike_count) {
        this.station_id = station_id;
        this.station_name = station_name;
        this.location = location;
        this.estimated_bike_count = estimated_bike_count;
    }
}
