package com.pasteyboi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Transfer {
    public static void download(User user, String id){
        URL url;
        HttpURLConnection con;
        try {
            url = new URL("http://localhost:8000/example.json");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
                    
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            try {
                JSONParser parser = new JSONParser();
                Object jsonObj = parser.parse(content.toString());

                JSONObject jsonObject = (JSONObject) jsonObj;
            } catch (ParseException e){
                System.err.println(e);
            }

        } catch (IOException e){
            System.err.println(e);
            System.exit(1);
        }
    }
}