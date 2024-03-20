module com.example.calculation {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.apache.commons.compress;
    requires org.apache.commons.io;
    requires org.apache.commons.collections4;
    requires org.apache.commons.codec;
    requires fastexcel.reader;
    requires fastexcel;
    requires commons.math3;
    requires vavr;
    requires org.apache.logging.log4j;
    requires log4j;

    opens com.example.calculation;
}