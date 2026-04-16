package com.laughingalpaca.bikeviewapp.Model;

import java.time.Instant;

//TODO: Finish this class so we can do get/set for the variables below
// Should also have methods for getting the ride length(the Citi bike data includes a start and end time, use this data to come up with te ride_length)
public class Ride {
    String ride_id;
    RideableType rideable_type;
    Station start_station;
    Station end_station;
    float ride_length;

    public Ride(String rideId, RideableType rideableType, Station startStation, Station endStation, Instant startedAt, Instant endedAt) {
    }

    //TODO: RETURN TO THIS
    public Station getStart_station() {
        return null;
    }

    public Station getEnd_station() {
        return null;
    }
}
