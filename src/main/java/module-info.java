module com.example.newnomads {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires java.desktop;


    opens com.example.newnomads to javafx.fxml;
    exports com.example.newnomads;
    exports bazneTabele;
    opens bazneTabele to javafx.fxml;
}