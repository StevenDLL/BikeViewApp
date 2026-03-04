package com.laughingalpaca.bikeviewapp.Model;

import com.gluonhq.maps.MapPoint;

public class Station {
    public String station_id;
    public String station_name;
    public MapPoint location;
    public int estimated_bike_count;

    public Station(String station_id, String station_name, MapPoint location, int estimated_bike_count) {
        this.station_id = station_id;
        this.station_name = station_name;
        this.location = location;
        this.estimated_bike_count = estimated_bike_count;
    }
}
