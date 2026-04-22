package com.laughingalpaca.bikeviewapp;

import com.google.cloud.firestore.Firestore;

public final class FirestoreSeeder {
    private static final String USAGE = """
            Usage:
            mvn -q -DskipTests exec:java -Dexec.mainClass=com.laughingalpaca.bikeviewapp.FirestoreSeeder -Dexec.args="live"
            """;

    private FirestoreSeeder() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || !"live".equalsIgnoreCase(args[0])) {
            System.out.println(USAGE);
            return;
        }

        DataHandler dataHandler = DataHandler.getInstance();
        Firestore firestore = dataHandler.getFirestore();

        if (firestore == null) {
            throw new IllegalStateException("Firestore connection is unavailable. Check config/firebase-service-account.json");
        }

        GbfsSyncService gbfsSyncService = new GbfsSyncService();
        int stationCount = gbfsSyncService.syncLiveStationsToFirestore(firestore);

        System.out.println("Synced " + stationCount + " live stations into Firestore.");
    }
}
