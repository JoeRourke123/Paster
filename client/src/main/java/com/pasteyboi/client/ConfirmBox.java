package com.pasteyboi.client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class ConfirmBox {

    static String answer;
    static TextField entry = new TextField();
    static Stage window;


    public static String display(String message) {
        window = new Stage();

        //Blocks interaction with other windows until this one is dealt with
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Enter Name");
        window.setMinWidth(150);
        window.setMinHeight(150);

        Label label1 = new Label(message);


        Button yes = new Button("Confirm");
        yes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                answer = entry.getText();
                window.close();
            }
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label1, entry, yes);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }
}