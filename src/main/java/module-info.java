module com.laughingalpaca.bikeviewapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.gluonhq.maps;
    requires java.desktop;
    requires javafx.graphics;

    opens com.laughingalpaca.bikeviewapp to javafx.fxml;
    opens com.laughingalpaca.bikeviewapp.Controller to javafx.fxml;
    exports com.laughingalpaca.bikeviewapp.View;
    opens com.laughingalpaca.bikeviewapp.View to javafx.fxml;
    exports com.laughingalpaca.bikeviewapp;
}