package com.laughingalpaca.bikeviewapp.Model;

import com.gluonhq.maps.MapPoint;
import java.time.Instant;

public class Station {
    private String station_id;
    private String station_name;
    private MapPoint location;
    private int estimated_bike_count;
    private String borough;
    private String zip_code;
    private Instant last_updated;


}