package com.laughingalpaca.bikeviewapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gluonhq.maps.MapPoint;
import java.net.http.HttpClient;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.laughingalpaca.bikeviewapp.Model.Station;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    public List<Station> fetchLiveStations() throws IOException, InterruptedException {
        JsonNode stationInformationRoot = fetchJson(STATION_INFORMATION_URL);
        JsonNode stationStatusRoot = fetchJson(STATION_STATUS_URL);

        Map<String, JsonNode> statusByStationId = new HashMap<>();
        Iterator<JsonNode> stationStatusIterator = stationStatusRoot.path("data").path("stations").elements();
        while (stationStatusIterator.hasNext()) {
            JsonNode stationStatus = stationStatusIterator.next();
            String stationId = stationStatus.path("station_id").asText("");
            if (!stationId.isBlank()) {
                statusByStationId.put(stationId, stationStatus);
            }
        }

        Instant feedUpdatedAt = Instant.ofEpochSecond(stationStatusRoot.path("last_updated").asLong(Instant.now().getEpochSecond()));

        List<Station> stations = new ArrayList<>();
        Iterator<JsonNode> stationInformationIterator = stationInformationRoot.path("data").path("stations").elements();
        while (stationInformationIterator.hasNext()) {
            JsonNode stationInformation = stationInformationIterator.next();
            String stationId = stationInformation.path("station_id").asText("");
            String stationName = stationInformation.path("name").asText("");
            double latitude = stationInformation.path("lat").asDouble(Double.NaN);
            double longitude = stationInformation.path("lon").asDouble(Double.NaN);

            if (stationId.isBlank() || stationName.isBlank() || Double.isNaN(latitude) || Double.isNaN(longitude)) {
                continue;
            }

            JsonNode statusNode = statusByStationId.get(stationId);
            int bikeCount = statusNode == null ? 0 : statusNode.path("num_bikes_available").asInt(0);

            stations.add(new Station(stationId, stationName, new MapPoint(latitude, longitude), bikeCount, "", "", feedUpdatedAt));
        }

        geoBoundaryService.enrichStationsWithBoroughs(stations);
        return stations;
    }

}
