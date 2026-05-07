package com.laughingalpaca.bikeviewapp;

import com.gluonhq.maps.MapPoint;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.laughingalpaca.bikeviewapp.Model.Ride;
import com.laughingalpaca.bikeviewapp.Model.RideableType;
import com.laughingalpaca.bikeviewapp.Model.Station;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class DataHandler {
    private static final List<Path> SERVICE_ACCOUNT_PATHS = List.of(
            Path.of("config", "firebase-service-account.json"),
            Path.of("config", "firebase_info", "bikeviewappKey.json"),
            Path.of("config", "csc325--citibikeapp-firebase-adminsdk-fbsvc-af39f79319.json")
    );
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a").withZone(ZoneId.systemDefault());
    private static final DataHandler INSTANCE = new DataHandler();
    private final List<Station> cachedStations = new ArrayList<>();
    private final GbfsSyncService gbfsSyncService = new GbfsSyncService();
    private final GeoBoundaryService geoBoundaryService = new GeoBoundaryService();
    private Firestore firestore;
    private String projectId = "Unavailable";
    private String lastError = "Not connected";
    private Instant lastSuccessfulRefresh;
    private boolean usingLiveFallback;

    public static DataHandler getInstance() {
        return INSTANCE;
    }

    public DataHandler() {
        refreshConnectionStatus();
    }

    public synchronized boolean refreshConnectionStatus() {
        firestore = initializeFirestore();
        if (firestore == null) {
            return false;
        }
        try {
            firestore.collection("app_metadata").document("status").get().get();
            lastError = "";
            lastSuccessfulRefresh = Instant.now();
            return true;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            lastError = "Firestore connection check interrupted.";
            return false;
        } catch (ExecutionException | RuntimeException exception) {
            lastError = buildReadableError("Firestore connection failed.", exception);
            return false;
        }
    }

    public synchronized List<Station> getAllStations() {
        if (firestore == null && !refreshConnectionStatus()) {
            return loadStationsFromFallback();
        }
        try {
            QuerySnapshot querySnapshot = firestore.collection("stations").get().get();
            List<Station> stations = querySnapshot.getDocuments().stream()
                    .map(this::mapStation)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Station::getStation_name, String.CASE_INSENSITIVE_ORDER))
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            geoBoundaryService.enrichStationsWithBoroughs(stations);
            if (stations.isEmpty()) {
                return loadStationsFromFallback();
            }
            usingLiveFallback = false;
            cacheStations(stations);
            lastSuccessfulRefresh = Instant.now();
            lastError = "";
            return new ArrayList<>(cachedStations);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            lastError = "Station load interrupted.";
        } catch (ExecutionException | RuntimeException exception) {
            lastError = buildReadableError("Unable to load stations from Firestore.", exception);
        }
        return loadStationsFromFallback();
    }

    public synchronized List<String> getAllBoroughs() {
        Set<String> boroughs = new LinkedHashSet<>();
        for (Station station : getAllStations()) {
            if (station.getBorough() != null && !station.getBorough().isBlank()) {
                boroughs.add(station.getBorough());
            }
        }
        return new ArrayList<>(boroughs);
    }

    public synchronized List<String> getAllStationNames() {
        return getAllStations().stream()
                .map(Station::getStation_name)
                .filter(name -> name != null && !name.isBlank())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    public synchronized List<Ride> getRidesForStation(String stationId) {
        if (stationId == null || stationId.isBlank()) {
            return List.of();
        }
        if (firestore == null && !refreshConnectionStatus()) {
            return List.of();
        }
        try {
            QuerySnapshot querySnapshot = firestore.collection("rides").get().get();
            List<Ride> rides = new ArrayList<>();
            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                Ride ride = mapRide(document);
                if (ride == null) {
                    continue;
                }
                boolean matchesStart = ride.getStart_station() != null
                        && stationId.equalsIgnoreCase(ride.getStart_station().getStation_id());
                boolean matchesEnd = ride.getEnd_station() != null
                        && stationId.equalsIgnoreCase(ride.getEnd_station().getStation_id());
                if (matchesStart || matchesEnd) {
                    rides.add(ride);
                }
            }
            lastError = "";
            return rides;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            lastError = "Ride lookup interrupted.";
        } catch (ExecutionException | RuntimeException exception) {
            lastError = buildReadableError("Unable to load rides from Firestore.", exception);
        }
        return List.of();
    }

    public synchronized String getProjectDisplayName() {
        return projectId;
    }

    public synchronized String getDatabaseDisplayName() {
        if (usingLiveFallback) {
            return "Live Citi Bike Feed";
        }
        return firestore == null ? "Unavailable" : "Cloud Firestore";
    }

    public synchronized String getDatabaseHostDisplay() {
        if (usingLiveFallback) {
            return "gbfs.lyft.com";
        }
        return firestore == null ? "Offline" : "N/A";
    }

    public synchronized String getLastUpdatedDisplay() {
        Optional<Instant> metadataTime = getMetadataLastUpdated();
        if (metadataTime.isPresent()) {
            return DISPLAY_DATE_FORMAT.format(metadataTime.get());
        }
        Optional<Instant> stationTime = cachedStations.stream()
                .map(Station::getLast_updated)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder());
        if (stationTime.isPresent()) {
            return DISPLAY_DATE_FORMAT.format(stationTime.get());
        }
        if (lastSuccessfulRefresh != null) {
            return DISPLAY_DATE_FORMAT.format(lastSuccessfulRefresh);
        }
        return "Unavailable";
    }

    public synchronized String getStatusMessage() {
        if (usingLiveFallback) {
            return "Using live Citi Bike feed because Firestore is unavailable.";
        }
        if (firestore != null && (lastError == null || lastError.isBlank())) {
            return "Connected to Firestore.";
        }
        return lastError == null || lastError.isBlank() ? "Firestore connection unavailable." : lastError;
    }

    public synchronized Firestore getFirestore() {
        if (firestore == null) {
            refreshConnectionStatus();
        }
        return firestore;
    }

    public synchronized Optional<GeoBoundary> getZipBoundary(String zipCode) {
        return geoBoundaryService.getZipBoundary(zipCode);
    }

    public synchronized boolean isStationInsideBoundary(Station station, GeoBoundary boundary) {
        return geoBoundaryService.isStationInsideZip(station, boundary);
    }

    private Firestore initializeFirestore() {
        Path serviceAccountPath = resolveServiceAccountPath();
        if (serviceAccountPath == null) {
            lastError = "Missing Firebase key. Checked: " + SERVICE_ACCOUNT_PATHS;
            return null;
        }
        try (FileInputStream inputStream = new FileInputStream(serviceAccountPath.toFile())) {
            ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(inputStream);
            projectId = credentials.getProjectId() == null || credentials.getProjectId().isBlank()
                    ? projectId
                    : credentials.getProjectId();
            FirebaseApp firebaseApp;
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .setProjectId(projectId)
                        .build();
                firebaseApp = FirebaseApp.initializeApp(options);
            } else {
                firebaseApp = FirebaseApp.getInstance();
            }
            return FirestoreClient.getFirestore(firebaseApp);
        } catch (IOException | RuntimeException exception) {
            lastError = buildReadableError("Unable to initialize Firebase.", exception);
            return null;
        }
    }

    private Path resolveServiceAccountPath() {
        return SERVICE_ACCOUNT_PATHS.stream()
                .filter(Files::exists)
                .findFirst()
                .orElse(null);
    }

    private Optional<Instant> getMetadataLastUpdated() {
        if (firestore == null || usingLiveFallback) {
            return Optional.empty();
        }
        try {
            DocumentSnapshot snapshot = firestore.collection("app_metadata").document("status").get().get();
            Instant instant = extractInstant(snapshot, "lastUpdated")
                    .or(() -> extractInstant(snapshot, "last_updated"))
                    .orElse(null);
            return Optional.ofNullable(instant);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (ExecutionException | RuntimeException exception) {
            return Optional.empty();
        }
    }

    private Station mapStation(DocumentSnapshot document) {
        String stationId = readFirstString(document, "stationId", "station_id", "id");
        String stationName = readFirstString(document, "stationName", "station_name", "name");
        Double latitude = readFirstDouble(document, "latitude", "lat");
        Double longitude = readFirstDouble(document, "longitude", "lng", "lon");
        if (stationId == null || stationName == null || latitude == null || longitude == null) {
            return null;
        }
        Integer bikeCount = readFirstInteger(document, "estimatedBikeCount", "estimated_bike_count", "bikeCount");
        Instant lastUpdated = extractInstant(document, "lastUpdated")
                .or(() -> extractInstant(document, "last_updated"))
                .orElse(null);
        return new Station(
                stationId,
                stationName,
                new MapPoint(latitude, longitude),
                bikeCount == null ? 0 : bikeCount,
                defaultString(readFirstString(document, "borough")),
                defaultString(readFirstString(document, "zipCode", "zip_code", "zipcode")),
                lastUpdated
        );
    }

    private Ride mapRide(DocumentSnapshot document) {
        String rideId = readFirstString(document, "rideId", "ride_id");
        String rideTypeValue = readFirstString(document, "rideableType", "rideable_type");
        RideableType rideableType = parseRideableType(rideTypeValue);
        Station startStation = buildRideStation(document, "startStationId", "start_station_id",
                "startStationName", "start_station_name");
        Station endStation = buildRideStation(document, "endStationId", "end_station_id",
                "endStationName", "end_station_name");
        Instant startedAt = extractInstant(document, "startedAt")
                .or(() -> extractInstant(document, "started_at"))
                .orElse(null);
        Instant endedAt = extractInstant(document, "endedAt")
                .or(() -> extractInstant(document, "ended_at"))
                .orElse(null);
        if (rideId == null || rideableType == null) {
            return null;
        }
        return new Ride(rideId, rideableType, startStation, endStation, startedAt, endedAt);
    }

    private Station buildRideStation(DocumentSnapshot document, String idKey, String legacyIdKey,
                                     String nameKey, String legacyNameKey) {
        String stationId = readFirstString(document, idKey, legacyIdKey);
        String stationName = readFirstString(document, nameKey, legacyNameKey);
        if (stationId == null && stationName == null) {
            return null;
        }
        return new Station(defaultString(stationId), defaultString(stationName), new MapPoint(0, 0), 0);
    }

    private RideableType parseRideableType(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toLowerCase(Locale.US);
        if ("electric_bike".equals(normalized) || "electric-bike".equals(normalized) || "electric".equals(normalized)) {
            return RideableType.electric_bike;
        }
        if ("classic_bike".equals(normalized) || "classic-bike".equals(normalized) || "classic".equals(normalized)) {
            return RideableType.classic_bike;
        }
        return null;
    }

    private String readFirstString(DocumentSnapshot document, String... keys) {
        for (String key : keys) {
            Object value = document.get(key);
            if (value instanceof String stringValue && !stringValue.isBlank()) {
                return stringValue;
            }
        }
        return null;
    }

    private Double readFirstDouble(DocumentSnapshot document, String... keys) {
        for (String key : keys) {
            Object value = document.get(key);
            if (value instanceof Number number) {
                return number.doubleValue();
            }
        }
        return null;
    }

    private Integer readFirstInteger(DocumentSnapshot document, String... keys) {
        for (String key : keys) {
            Object value = document.get(key);
            if (value instanceof Number number) {
                return number.intValue();
            }
        }
        return null;
    }

    private Optional<Instant> extractInstant(DocumentSnapshot document, String key) {
        Object value = document.get(key);
        if (value instanceof Timestamp timestamp) {
            return Optional.of(timestamp.toDate().toInstant());
        }
        if (value instanceof java.util.Date date) {
            return Optional.of(date.toInstant());
        }
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            try {
                return Optional.of(Instant.parse(stringValue));
            } catch (RuntimeException ignored) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private List<Station> loadStationsFromFallback() {
        try {
            List<Station> liveStations = gbfsSyncService.fetchLiveStations();
            usingLiveFallback = true;
            cacheStations(liveStations);
            lastSuccessfulRefresh = Instant.now();
            return new ArrayList<>(cachedStations);
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return new ArrayList<>(cachedStations);
        }
    }

    private void cacheStations(List<Station> stations) {
        cachedStations.clear();
        cachedStations.addAll(stations);
    }

    private String buildReadableError(String prefix, Exception exception) {
        Throwable rootCause = exception;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        String message = rootCause.getMessage();
        if (message == null || message.isBlank()) {
            message = exception.getMessage();
        }
        if (message == null || message.isBlank()) {
            return prefix;
        }
        return prefix + " " + message;
    }
}

