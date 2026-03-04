module com.laughingalpaca.bikeviewapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.laughingalpaca.bikeviewapp to javafx.fxml;
    exports com.laughingalpaca.bikeviewapp;
}