package com.laughingalpaca.bikeviewapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gluonhq.maps.MapPoint;
import com.laughingalpaca.bikeviewapp.Model.Station;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class GeoBoundaryService {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);
    private static final int MAX_JSON_RESPONSE_BYTES = 10 * 1024 * 1024;
    private static final Pattern ZIP_CODE_PATTERN = Pattern.compile("\\d{5}");
    private static final String BOROUGH_BOUNDARIES_URL =
            "https://tigerweb.geo.census.gov/arcgis/rest/services/TIGERweb/State_County/MapServer/9/query"
                    + "?where=STATE%20%3D%20'36'%20AND%20COUNTY%20IN%20('005','047','061','081','085')"
                    + "&outFields=NAME,COUNTY&returnGeometry=true&f=geojson";
    private static final String ZIP_BOUNDARY_URL_TEMPLATE =
            "https://tigerweb.geo.census.gov/arcgis/rest/services/TIGERweb/tigerWMS_Current/MapServer/2/query"
                    + "?where=%s&outFields=ZCTA5&returnGeometry=true&f=geojson";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private List<GeoBoundary> boroughBoundaries;
    private final Map<String, Optional<GeoBoundary>> zipBoundaryCache = new HashMap<>();

    public GeoBoundaryService() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .build();
        objectMapper = new ObjectMapper();
    }

    public synchronized List<String> enrichStationsWithBoroughs(List<Station> stations) {
        List<String> warnings = new ArrayList<>();
        try {
            ensureBoroughBoundariesLoaded();
            for (Station station : stations) {
                String borough = resolveBorough(station.getLocation());
                station.setBorough(borough);
            }
        } catch (IOException | InterruptedException exception) {
            warnings.add("Unable to load borough boundaries.");
        }
        return warnings;
    }

    public synchronized Optional<GeoBoundary> getZipBoundary(String zipCode) {
        if (zipCode == null || zipCode.isBlank()) {
            return Optional.empty();
        }
        String normalizedZip = zipCode.trim();
        if (!ZIP_CODE_PATTERN.matcher(normalizedZip).matches()) {
            return Optional.empty();
        }
        if (zipBoundaryCache.containsKey(normalizedZip)) {
            return zipBoundaryCache.get(normalizedZip);
        }
        try {
            String whereClause = URLEncoder.encode("ZCTA5 = '" + normalizedZip + "'", StandardCharsets.UTF_8);
            String url = ZIP_BOUNDARY_URL_TEMPLATE.formatted(whereClause);
            JsonNode root = fetchJson(url);
            List<GeoBoundary> boundaries = parseFeatureCollection(root, "ZCTA5", false);
            Optional<GeoBoundary> boundary = boundaries.stream().findFirst();
            zipBoundaryCache.put(normalizedZip, boundary);
            return boundary;
        } catch (IOException | InterruptedException exception) {
            zipBoundaryCache.put(normalizedZip, Optional.empty());
            return Optional.empty();
        }
    }

    public String resolveBorough(MapPoint point) {
        if (point == null) {
            return "";
        }
        try {
            ensureBoroughBoundariesLoaded();
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return "";
        }
        for (GeoBoundary boundary : boroughBoundaries) {
            if (boundary.contains(point)) {
                return boundary.label();
            }
        }
        return "";
    }

    public boolean isStationInsideZip(Station station, GeoBoundary zipBoundary) {
        return station != null
                && station.getLocation() != null
                && zipBoundary != null
                && zipBoundary.contains(station.getLocation());
    }

    private void ensureBoroughBoundariesLoaded() throws IOException, InterruptedException {
        if (boroughBoundaries != null) {
            return;
        }
        JsonNode root = fetchJson(BOROUGH_BOUNDARIES_URL);
        boroughBoundaries = parseFeatureCollection(root, "COUNTY", true);
    }

    private JsonNode fetchJson(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(REQUEST_TIMEOUT)
                .GET()
                .header("Accept", "application/json")
                .header("User-Agent", "BikeViewApp/1.0")
                .build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Boundary request failed with status " + response.statusCode());
        }
        if (response.body().length > MAX_JSON_RESPONSE_BYTES) {
            throw new IOException("Boundary response exceeded the allowed size.");
        }
        return objectMapper.readTree(response.body());
    }

    private List<GeoBoundary> parseFeatureCollection(JsonNode root, String propertyName, boolean mapCountyToBorough) {
        List<GeoBoundary> boundaries = new ArrayList<>();
        for (JsonNode feature : root.path("features")) {
            JsonNode properties = feature.path("properties");
            String id = properties.path(propertyName).asText("");
            if (id.isBlank()) {
                continue;
            }
            String label = id;
            if (mapCountyToBorough) {
                label = mapCountyToBorough(properties.path("NAME").asText(""), id);
            }
            JsonNode geometry = feature.path("geometry");
            List<List<MapPoint>> rings = parseGeometryRings(geometry);
            if (rings.isEmpty()) {
                continue;
            }
            double minLatitude = Double.POSITIVE_INFINITY;
            double maxLatitude = Double.NEGATIVE_INFINITY;
            double minLongitude = Double.POSITIVE_INFINITY;
            double maxLongitude = Double.NEGATIVE_INFINITY;
            for (List<MapPoint> ring : rings) {
                for (MapPoint point : ring) {
                    minLatitude = Math.min(minLatitude, point.getLatitude());
                    maxLatitude = Math.max(maxLatitude, point.getLatitude());
                    minLongitude = Math.min(minLongitude, point.getLongitude());
                    maxLongitude = Math.max(maxLongitude, point.getLongitude());
                }
            }
            boundaries.add(new GeoBoundary(id, label, rings, minLatitude, maxLatitude, minLongitude, maxLongitude));
        }
        return boundaries;
    }

    private List<List<MapPoint>> parseGeometryRings(JsonNode geometry) {
        List<List<MapPoint>> rings = new ArrayList<>();
        String type = geometry.path("type").asText("");
        JsonNode coordinates = geometry.path("coordinates");
        if ("Polygon".equalsIgnoreCase(type)) {
            addPolygonRings(rings, coordinates);
        } else if ("MultiPolygon".equalsIgnoreCase(type)) {
            for (JsonNode polygonNode : coordinates) {
                addPolygonRings(rings, polygonNode);
            }
        }
        return rings;
    }

    private void addPolygonRings(List<List<MapPoint>> rings, JsonNode polygonCoordinates) {
        for (JsonNode ringNode : polygonCoordinates) {
            List<MapPoint> ring = new ArrayList<>();
            for (JsonNode coordinateNode : ringNode) {
                if (coordinateNode.size() < 2) {
                    continue;
                }
                double longitude = coordinateNode.get(0).asDouble();
                double latitude = coordinateNode.get(1).asDouble();
                ring.add(new MapPoint(latitude, longitude));
            }
            if (ring.size() >= 3) {
                rings.add(ring);
            }
        }
    }

    private String mapCountyToBorough(String countyName, String countyCode) {
        return switch (countyCode) {
            case "005" -> "Bronx";
            case "047" -> "Brooklyn";
            case "061" -> "Manhattan";
            case "081" -> "Queens";
            case "085" -> "Staten Island";
            default -> countyName == null ? "" : countyName.trim();
        };
    }
}
