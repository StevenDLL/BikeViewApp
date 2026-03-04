package com.laughingalpaca.bikeviewapp.Model;

//TODO: Finish this class so we can do get/set for the variables below
// Should also have methods for getting the ride length(the Citi bike data includes a start and end time, use this data to come up with te ride_length)
public class Ride {
    String ride_id;
    RideableType rideable_type;
    Station start_station;
    Station end_station;
    float ride_length;
}
