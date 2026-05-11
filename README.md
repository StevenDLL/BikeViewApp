# BikeViewApp 

# Project Manager: Steven Acosta
# Group Members: Sam Batra, Farzaan Patwary, Daniel Yakobi

BikeViewApp is a JavaFX desktop application for viewing New York City Citi Bike station data on an interactive map. The project was built as a college software engineering course project, with a focus on layered design, real data integration, map-based interaction, and practical demo readiness.

The application loads station information from Cloud Firestore when configured, falls back to the live Citi Bike GBFS feed when Firestore is unavailable, enriches stations with geographic boundary data, and lets users filter the map by ZIP code, borough, station, and available bike count.

## Project Goals

- Display Citi Bike stations across New York City using an interactive JavaFX map.
- Connect the desktop UI to real station data instead of hardcoded sample data.
- Support useful map filters for narrowing station results.
- Show station details through hover and click interactions.
- Keep the architecture understandable for a software engineering course presentation.
- Document demo security limits honestly, especially around local Firebase credentials.

## Core Features

- Launcher screen with project branding and backend freshness information.
- Interactive map view powered by Gluon Maps.
- Station markers for Citi Bike locations.
- Hover tooltips and a station information panel for selected stations.
- Filter controls for ZIP code, borough, station name, and minimum bike count.
- Boundary overlay support for ZIP-based filtering.
- Firestore-backed station loading with live GBFS fallback.
- Firestore seeding utility for syncing live station data into the demo database.

## Technology Stack

- Java 17+
- JavaFX 21
- Maven Wrapper
- Gluon Maps
- Firebase Admin SDK
- Google Cloud Firestore
- Jackson JSON parsing
- Citi Bike GBFS public data feed
- U.S. Census TIGERweb boundary data

## Software Architecture

BikeViewApp follows a layered structure so the UI, data access, map rendering, and external services remain understandable as separate responsibilities.

| Layer | Main Files | Responsibility |
| --- | --- | --- |
| Application entry | `AppEntry`, `Launcher`, `SceneManager` | Starts the JavaFX application and switches between FXML scenes. |
| UI controllers | `AppLauncherController`, `MainController` | Handles screen events, filter controls, map refreshes, and displayed status text. |
| Data gateway | `DataHandler` | Central access point for station data, Firestore status, fallback behavior, and boundary lookups. |
| Live data sync | `GbfsSyncService`, `FirestoreSeeder` | Reads Citi Bike GBFS station feeds and optionally writes station data into Firestore. |
| Geographic logic | `GeoBoundary`, `GeoBoundaryService`, `BoundaryMapLayer` | Loads Census boundary geometry, checks station containment, and draws ZIP overlays. |
| Map rendering | `StationMapLayer`, `BoundaryMapLayer` | Places station markers and boundary polygons on the Gluon map. |
| Data models | `Station`, `Ride`, `RideableType` | Represents station and ride-related data used by the app. |

## Data Flow

The expected demo data flow is:

```text
Citi Bike GBFS feed
        |
        v
GbfsSyncService / FirestoreSeeder
        |
        v
Cloud Firestore
        |
        v
DataHandler
        |
        v
JavaFX controllers and map layers
```

When Firestore credentials are not configured or Firestore is unavailable, `DataHandler` attempts to load station data directly from the live Citi Bike GBFS feed. This keeps the app useful for classroom demos even when the local database setup is incomplete.

## Demo Walkthrough

The following demo assets are intentionally kept in the README because they show the project manager's required visual flow.

### Launch

The launcher introduces the application and shows the last available data update.

<img width="377" height="587" alt="Launch" src="https://github.com/user-attachments/assets/817c7d36-7068-4fb5-bc2d-e7c53a80c3d4" />

### Map View

The main screen displays Citi Bike stations across New York City on an interactive map.

<img width="1282" height="752" alt="MapView" src="https://github.com/user-attachments/assets/0c35e113-1d9d-4ce3-9552-0376de708d29" />

### Interactive Hover and Info Panel

Users can hover over station markers and click stations to view details such as station ID, station name, coordinates, and available bikes.

<img width="1282" height="752" alt="Station_Info_View_And_Hover" src="https://github.com/user-attachments/assets/b05b90ec-4db6-46cd-afa3-2f5e50043288" />

### Filter by ZIP Code

ZIP filtering uses geographic boundary data to identify stations inside a selected ZIP area.

<img width="1282" height="752" alt="Filter_By_Zipcode" src="https://github.com/user-attachments/assets/cb7fa8ad-1bb4-475f-b44b-49d892f7a44d" />

### Filter by Borough

Borough filtering narrows the map to stations in a selected New York City borough.

<img width="1282" height="752" alt="Filter_By_Borough" src="https://github.com/user-attachments/assets/91ce9b08-c0f6-4a51-af76-12410c261173" />

### Filter by Station

Station filtering allows users to focus the map on a specific station.

<img width="1282" height="752" alt="Filter_By_Station" src="https://github.com/user-attachments/assets/6d42502c-6eb9-4b09-a0c0-9eeb4a267e68" />

### Mix and Match Filters

Multiple filters can be combined to narrow station results more precisely.

<img width="1282" height="752" alt="Filter_By_Multiple" src="https://github.com/user-attachments/assets/3a20201c-d8bc-421b-a8f8-609d3bd9c6fc" />

## Setup and Run Instructions

### Prerequisites

- Windows, macOS, or Linux with Java 17 or newer installed.
- Internet access for live Citi Bike data, map tiles, and Census boundary data.
- Optional Firebase service-account JSON if using Firestore for the demo database.

### Run the Application

On Windows PowerShell:

```powershell
.\mvnw.cmd clean javafx:run
```

Or use the helper script:

```powershell
.\run-app.cmd
```

The helper script checks for `JAVA_HOME`, attempts to find a supported local JDK, and then launches the JavaFX app through the Maven wrapper.

### Compile Without Launching

```powershell
.\mvnw.cmd -q clean -DskipTests compile
```

### Seed Firestore With Live Station Data

If a local Firebase Admin service-account JSON is configured, Firestore can be seeded from the live Citi Bike feed:

```powershell
.\mvnw.cmd -q -DskipTests exec:java -Dexec.mainClass=com.laughingalpaca.bikeviewapp.FirestoreSeeder -Dexec.args="live"
```

This writes station documents to `stations/{stationId}` and updates `app_metadata/status` with the latest sync metadata.

## Security and Configuration

This project can use a local Firebase Admin service-account JSON for class and demo runs. Keep those files under `config/` and never commit them.

The app currently checks for local credential files in these locations:

- `config/firebase-service-account.json`
- `config/firebase_info/bikeviewappKey.json`
- `config/csc325--citibikeapp-firebase-adminsdk-fbsvc-af39f79319.json`

For demo credentials, use least-privilege access limited to the app's station sync data. A production desktop release should move Firebase access behind a backend API, or use Firebase client authentication with Firestore security rules instead of shipping an Admin SDK key with the app.

## Current Limitations and Future Work

- The routes tab is present in the UI, but full route planning and trip analytics should be treated as future work.
- `Ride.java` is still incomplete, so ride history, ride duration, and route-based features should not be presented as finished.
- There is currently no `src/test` directory, so automated test coverage still needs to be added.
- Firestore credentials are suitable for a controlled class demo only, not a production desktop distribution.
- Error handling could be expanded with clearer user-facing messages for network failures, missing credentials, and unavailable boundary services.

## Verification

The project currently compiles successfully with:

```powershell
.\mvnw.cmd -q clean -DskipTests compile
```
