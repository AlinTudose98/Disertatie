package com.alint.disertatie.javaeutlclient.controller;

import com.alint.disertatie.javaeutlclient.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.IOException;

public class HomeView {

    boolean inTlBrowser = false;
    boolean inValSign = false;
    boolean inValCer = false;


    @FXML
    protected HBox historyTree;

    @FXML
    protected ImageView logoImageView;

    @FXML
    protected Hyperlink currWindowView;
    @FXML
    protected Label lowerRightStatusLabel;

    @FXML
    protected AnchorPane content;

    @FXML
    protected void initialize() throws IOException {
        lowerRightStatusLabel.setText("\u00a9 Tudose Alin-Romeo 2023");
        currWindowView.setText("Main Menu");

        Pane welcomePane = FXMLLoader.load(MainApplication.class.getResource("view/welcome-view.fxml"));
        welcomePane.setPrefHeight(content.getPrefHeight());
        welcomePane.setPrefWidth(content.getPrefWidth());
        content.getChildren().add(welcomePane);
    }


    @FXML
    protected void returnToMainMenu(ActionEvent event) throws IOException {
        inTlBrowser=false;
        inValCer=false;
        inValSign=false;

        Hyperlink mainMenuRet = (Hyperlink)historyTree.getChildren().get(0);
        historyTree.getChildren().clear();
        historyTree.getChildren().add(mainMenuRet);

        Pane welcomePane = FXMLLoader.load(MainApplication.class.getResource("view/welcome-view.fxml"));
        content.getChildren().clear();
        content.getChildren().add(welcomePane);
        VBox.setVgrow(welcomePane, Priority.ALWAYS);
    }
    @FXML
    public void goToEuTLBrowser(ActionEvent event) throws IOException {
        if(!inTlBrowser) {
            inTlBrowser=true;
            inValCer=false;
            inValSign=false;
            getTreeEntry("TL Browser");

            Pane pane = FXMLLoader.load(MainApplication.class.getResource("view/tlbrowser-view.fxml"));
            content.getChildren().clear();
            content.getChildren().add(pane);
            VBox.setVgrow(content,Priority.ALWAYS);
            VBox.setVgrow(pane, Priority.ALWAYS);
        }
    }

    @FXML
    public void goToValidateSignature(ActionEvent event) throws IOException {
        if(!inValSign) {
            inTlBrowser=false;
            inValCer=false;
            inValSign=true;
            getTreeEntry("Signature Validator");

            Pane pane = FXMLLoader.load(MainApplication.class.getResource("view/valsig-view.fxml"));
            content.getChildren().clear();
            content.getChildren().add(pane);
            VBox.setVgrow(content,Priority.ALWAYS);
            VBox.setVgrow(pane, Priority.ALWAYS);
        }
    }

    @FXML
    public void goToValidateCertificate(ActionEvent event) throws IOException {
        if(!inValCer) {
            inTlBrowser=false;
            inValCer=true;
            inValSign=false;
            getTreeEntry("Certificate Validator");

            Pane pane = FXMLLoader.load(MainApplication.class.getResource("view/valcer-view.fxml"));
            content.getChildren().clear();
            content.getChildren().add(pane);
            VBox.setVgrow(content,Priority.ALWAYS);
            VBox.setVgrow(pane, Priority.ALWAYS);
        }
    }


    protected void getSeparator() {
        Label label = new Label("\u226b");
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(new Font(11));
        label.setTextFill(Color.color(0.325, 0.325, 0.325));
        label.setMaxHeight(1.7976931348623157E308);
        label.setMaxWidth(-1);
        historyTree.getChildren().add(label);
    }

    protected void getTreeEntry(String text) throws IOException {
        returnToMainMenu(null);
        getSeparator();

        Hyperlink hy = new Hyperlink(text);
        hy.setMaxHeight(1.7976931348623157E308);
        hy.setFont(new Font(13));
        hy.setTextFill(Color.color(0,0,0));
        hy.setEffect(new DropShadow(2, Color.color(1,1,1)));
        historyTree.getChildren().add(hy);
    }
}