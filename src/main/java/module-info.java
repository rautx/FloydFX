module com.example.floydfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.floydfx to javafx.fxml;
    exports com.example.floydfx;
}