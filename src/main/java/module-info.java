module com.laughingalpaca.bikeviewapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.gluonhq.maps;
    requires java.desktop;
    requires javafx.graphics;
    requires firebase.admin;
    requires org.slf4j.simple;
    requires com.google.auth;
    requires com.google.auth.oauth2;
    requires com.google.api.apicommon;
    requires google.cloud.core;
    requires google.cloud.firestore;
    requires java.net.http;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;


    opens com.laughingalpaca.bikeviewapp to javafx.fxml;
    opens com.laughingalpaca.bikeviewapp.Controller to javafx.fxml;
    exports com.laughingalpaca.bikeviewapp.View;
    opens com.laughingalpaca.bikeviewapp.View to javafx.fxml;
    exports com.laughingalpaca.bikeviewapp;
}