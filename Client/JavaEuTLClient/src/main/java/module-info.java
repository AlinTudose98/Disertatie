module com.alint.disertatie.javaeutlclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires lombok;
    requires jpms_dss_jaxb_parsers;

    opens com.alint.disertatie.javaeutlclient to javafx.fxml;
    exports com.alint.disertatie.javaeutlclient;
    exports com.alint.disertatie.javaeutlclient.controller;
    opens com.alint.disertatie.javaeutlclient.controller to javafx.fxml;
}