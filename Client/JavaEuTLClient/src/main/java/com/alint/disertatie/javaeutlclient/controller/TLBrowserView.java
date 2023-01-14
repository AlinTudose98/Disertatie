package com.alint.disertatie.javaeutlclient.controller;

import com.alint.disertatie.javaeutlclient.model.entity.ListOfTrustedLists;
import com.alint.disertatie.javaeutlclient.util.ServerRetriever;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TLBrowserView {

    @FXML
    public HBox mainFrame;
    @FXML
    public ScrollPane content;
    @FXML
    public Label label;
    protected ListOfTrustedLists lotl;

    public TLBrowserView() throws IOException {
        ServerRetriever serverRetriever = new ServerRetriever();
        lotl = serverRetriever.getListOfTrustedLists();
    }

    @FXML
    public void initialize() {
        label.textProperty().set(lotl.toString());
        content.setPrefHeight(mainFrame.getPrefHeight());
        content.setPrefWidth(mainFrame.getPrefWidth());
    }
}
