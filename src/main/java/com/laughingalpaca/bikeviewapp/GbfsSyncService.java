package com.laughingalpaca.bikeviewapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gluonhq.maps.MapPoint;
import java.net.http.HttpClient;

public final class GbfsSyncService {

    public static final String STATION_INFORMATION_URL = "https://gbfs.lyft.com/gbfs/1.1/bkn/en/station_information.json";
    public static final String STATION_STATUS_URL = "https://gbfs.lyft.com/gbfs/1.1/bkn/en/station_status.json";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final GeoBoundaryService geoBoundaryService;

    public GbfsSyncService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.geoBoundaryService = new GeoBoundaryService();
    }
}