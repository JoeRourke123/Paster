package main.java.com.pasteyboi.client;

import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Client extends Application {
    public void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane root = new GridPane();

        TextField username = new TextField();
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        Button login = new Button("Login");

        Hyperlink guestLabel = new Hyperlink("Guest? Click here to use 3 Keywords instead");

        
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
