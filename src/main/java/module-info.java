module com.example.calculation {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;


    opens com.example.calculation to javafx.fxml;
    exports com.example.calculation;
}