module com.alint.disertatie.javaeutlclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires com.fasterxml.jackson.databind;
    requires org.kordamp.bootstrapfx.core;


    opens com.alint.disertatie.javaeutlclient to javafx.fxml;
    opens com.alint.disertatie.javaeutlclient.controller to javafx.fxml;
    opens com.alint.disertatie.client.eutlwebview.model.entity to com.fasterxml.jackson.databind;
    opens com.alint.disertatie.client.eutlwebview.model.enums to com.fasterxml.jackson.databind;
    opens com.alint.disertatie.client.eutlwebview.model.message to com.fasterxml.jackson.databind;
    exports com.alint.disertatie.javaeutlclient;
    exports com.alint.disertatie.javaeutlclient.controller;
    exports com.alint.disertatie.client.eutlwebview.model.entity to com.fasterxml.jackson.databind;
    exports com.alint.disertatie.client.eutlwebview.model.enums to com.fasterxml.jackson.databind;
    exports com.alint.disertatie.client.eutlwebview.model.message to com.fasterxml.jackson.databind;
}