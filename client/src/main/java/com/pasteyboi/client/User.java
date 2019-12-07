package com.pasteyboi.client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    private String username, hashedPassword, userID;
    private boolean auth = false;

    public User(){}

    public User(String username, String password) {
        this.username = username;
        this.hashedPassword = hash(password);
        this.userID = hash(username);

        URL url;
        HttpURLConnection con;
        try {
            url = new URL("https://pasteyboi.appspot.com/signinUser");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            JSONObject dump = new JSONObject();
            dump.put("userName", getUsername());
            dump.put("password", getHashedPassword());

            String json = dump.toJSONString();

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                if(response.toString().equals("200")) {
                    this.auth = true;
                }
            }

        } catch (IOException e) {
            System.err.println(e);
            System.exit(-1);
        }
    }

    public boolean getAuth() {
        return auth;
    }

    public String getUsername() {
        return username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUsername(String username) {
        this.userID = hash(username);
        this.username = username;
    }

    public String getHashedPassword() {
        return this.hashedPassword;
    }

    public void setPassword(String password){
        this.hashedPassword = hash(password);
    }

    public String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(byte i : hash) {
                sb.append(String.format("%02x", i));
            }
    
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return password;
    }
}