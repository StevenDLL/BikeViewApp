package com.laughingalpaca.bikeviewapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gluonhq.maps.MapPoint;
import java.net.http.HttpClient;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;

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

    private JsonNode fetchJson(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .header("User-Agent", "BikeViewApp/1.0")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("GBFS request failed with status " + response.statusCode() + " for " + url);
        }
        return objectMapper.readTree(response.body());
    }
}
