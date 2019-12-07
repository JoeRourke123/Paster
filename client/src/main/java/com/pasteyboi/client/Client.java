package main.java.com.pasteyboi.client;

import javafx.application.Application;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Scanner;

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
        Label text = new Label("Enter text or drag and drop files");
        TextArea target = new TextArea();
        target.setPrefSize(500,500);
        target.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if(event.getGestureSource() != target && event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });
        target.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if(db.hasFiles()) {
                    target.appendText(getFiles(db.getFiles()));
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
        root.getChildren().addAll(target, text);

        Scene scene = new Scene(root);
        return scene;
    }

    public String getFiles(Collection<File> path) {
        String out = "";
        for (File file : path) {
            try {
                Scanner fileread = new Scanner(file);
                while(fileread.hasNextLine()) {
                    out += fileread.nextLine() + "\n";
                }
            }catch(Exception e) {
                System.out.println("Not a text file");
            }
        }
        return out;
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

