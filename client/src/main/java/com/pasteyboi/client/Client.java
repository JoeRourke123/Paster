package main.java.com.pasteyboi.client;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Client extends Application {

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(textDump());
        primaryStage.show();
    }


    public Scene LoginScene() {
        GridPane root = new GridPane();

        TextField username = new TextField();
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        Button login = new Button("Login");
        login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        Hyperlink guestLabel = new Hyperlink("Login as guest");

        Scene scene = new Scene(root);

        return scene;
    }


    public Scene textDump() {
        VBox root = new VBox(5);


        StackPane dump = new StackPane();
        Label text = new Label("LKFJAL;KDGF");
        TextField target = new TextField();
        target.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                System.out.println(event.getGestureSource() + "___" + event.getDragboard());
            }
        });
        root.getChildren().addAll(target, text);

        Scene scene = new Scene(root);
        return scene;
    }

    public String hashPass(String pass) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(pass.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for(byte i:hash) {
            sb.append(String.format("%02x", i));
        }
        return sb.toString();
    }

}

