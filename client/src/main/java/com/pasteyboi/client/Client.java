package com.pasteyboi.client;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.json.simple.*;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import sun.reflect.generics.tree.Tree;

public class Client extends Application {

    User user;
    Stage stage;
    JSONObject selectedDump;
    JSONObject selectedText;
    JSONArray currentUserDumps;
    Scene userdashboard;
    Scene login;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        login = LoginScene();

        stage.setScene(login);
        stage.show();
    }

    public Scene LoginScene() {
        GridPane root = new GridPane();

        final Label message = new Label("");
        final TextField username = new TextField();
        final PasswordField password = new PasswordField();
        password.setPromptText("Password");
        Button login = new Button("Login");
        login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                    boolean success = false;
                    if(new User(username.getText(), password.getText()).getAuth()) {
                        user = new User(username.getText(), password.getText());
                        success = true;
                        System.out.println("VALID INPUT");
                    }
                    else {
                        System.out.println("INVALID INPUT");
                        message.setText("Enter a valid username and password");
                    }
                    if(success) {
                        currentUserDumps = Transfer.getUserDumps(user);
                        if(currentUserDumps.size() != 0) {
                            System.out.println("GOT DATA");
                        }
                        else {
                            System.out.println("NO DATA");
                        }
                        userdashboard = userDashboard();
                        stage.setScene(userdashboard);
                    }
            }
        });
        root.add(message, 0, 0);
        root.add(username, 0, 1);
        root.add(password, 0, 2);
        root.add(login, 0, 3);

        Scene scene = new Scene(root, 300, 300);

        return scene;
    }

    public Scene userDashboard() {
        selectedDump = (JSONObject) ((JSONArray)currentUserDumps).get(0);
        System.out.println(selectedDump);
        selectedText = (JSONObject) ((JSONArray)selectedDump.get("contents")).get(0);



        final TextArea target = new TextArea();
        TreeView dumps = new TreeView();
        TreeItem rootItem = new TreeItem(user.getUsername());

        for (Object dump: currentUserDumps) {
            TreeItem item = new TreeItem(((JSONObject)dump).get("dumpID"));
            JSONArray texts = (JSONArray)((JSONObject)dump).get("contents");
            for(Object text: texts) {
                item.getChildren().add(new TreeItem(((JSONObject)text).get("fileName")));
            }
            rootItem.getChildren().add(item);
        }
        dumps.setRoot(rootItem);

        dumps.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                for (Object o: currentUserDumps) {
                    if((((JSONObject) o).get("dumpID").equals(((TreeItem)newValue).getParent().getValue()))) {
                        selectedDump = (JSONObject)o;
                    }
                }
                System.out.println(newValue);
                JSONArray dumpContents = (JSONArray) (selectedDump.get("contents"));
                for(Object o : dumpContents) {
                    if (((JSONObject) o).get("fileName").equals(((TreeItem) newValue).getValue())) {
                        selectedText = (JSONObject) o;
                    }
                }

                target.setText((String) ((JSONObject)selectedText).get("body"));
            }
        });

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
                    target.appendText(dumpFiles(db.getFiles(), selectedDump));
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });

        Button save = new Button("SAVE CHANGES");
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedText.put("body", target.getText());
                Transfer.upload(user, (JSONArray) selectedDump.get("contents"), (String)selectedDump.get("dumpID"));
            }
        });
        final Button newText = new Button("NEW TEXT");
        newText.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Save current text
                selectedText.put("body", target.getText());
                Transfer.upload(user, (JSONArray) selectedDump.get("contents"), (String)selectedDump.get("dumpID"));

                //New text
                JSONObject newtext = new JSONObject();
                target.setText("");
                newtext.put("fileIndex", selectedDump.size());
                newtext.put("body", "");
                selectedDump.put("contents", newtext);
            }
        });

        final Button newDump = new Button("NEW DUMP");
        newDump.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Save current text
                selectedText.put("body", target.getText());
                Transfer.upload(user, (JSONArray) selectedDump.get("contents"), (String)selectedDump.get("dumpID"));

                JSONObject newDump = new JSONObject();
                String dumpID = Transfer.generateDumpID();
                newDump.put("dumpID", dumpID);
                newDump.put("userID", user.getUserID());

                newDump.put("contents", null);

                currentUserDumps.add(newDump);
                selectedDump = (JSONObject) currentUserDumps.get(currentUserDumps.size()-1);
            }
        });

        VBox root = new VBox(10);

        HBox stuff = new HBox(1);
        stuff.getChildren().addAll(dumps, target);

        HBox buttons = new HBox(10);
        stuff.getChildren().addAll(newText, newDump, save);

        root.getChildren().addAll(stuff, buttons);

        Scene scene = new Scene(root);
        return scene;
    }


    public String dumpFiles(Collection<File> path, JSONObject currentDump) {
        JSONArray currentDumpArray = (JSONArray)currentDump.get("contents");
        int index = currentDumpArray.size();
        String out = "";
        for (File file : path) {
            try {
                JSONObject newfile = new JSONObject();
                Scanner fileread = new Scanner(file);
                newfile.put("fileIndex", index);
                index++;
                String[] filename = file.getParent().split("/");
                newfile.put("fileName", filename[filename.length-1]);
                while(fileread.hasNextLine()) {
                    out += fileread.nextLine() + "\n";
                }
                newfile.put("body", out);
                currentDumpArray.add(file);
            }catch(Exception e) {
                System.out.println("Not a text file");
            }
        }
        return out;
    }
}