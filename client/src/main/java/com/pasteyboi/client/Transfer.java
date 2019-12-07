package com.pasteyboi.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Transfer {
    public static JSONObject download(User user, String id) {
        URL url;
        HttpURLConnection con;
        try {
            url = new URL("https://pasteyboi.appspot.com/getDump?userID=" + user.getUserID() + "&dumpID=" + id);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuffer content = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            return (JSONObject) (new JSONParser()).parse(content.toString());

        } catch (IOException | ParseException e){
            System.err.println(e);
            System.exit(1);
        }

        return new JSONObject();
    }

    public static String generateDumpID() {
        String[] words = { "boot", "work", "term", "hike", "fare", "sign", "crop", "post", "part", "cut", "able",
                "pain", "poll", "nest", "wear", "quit", "snow", "go", "fog", "pour", "tap", "home", "view", "chop",
                "maid", "ice", "area", "fuel", "swop", "hand", "flat", "herd", "hold", "duke", "tent", "lie", "lift",
                "easy", "full", "pit", "dead", "mail", "bolt", "dive", "lay", "fish", "low", "line", "hill", "heat",
                "side", "tube", "ruin", "stay", "add", "rain", "bare", "bay", "miss", "tie", "hay", "use", "belt",
                "lamb", "hall", "root", "flow", "city", "van", "jet", "gear", "cage", "bold", "leg", "tile", "camp",
                "head", "cash", "race", "lamp", "tree", "vat", "pole", "see", "make", "ally", "roll", "pump", "drop",
                "pan", "dish", "hair", "silk", "spin", "load", "pier", "deer", "dorm", "team", "stun", "sick", "door",
                "trip", "drum", "whip", "left", "fate", "beat", "bed", "palm", "fame", "echo", "land", "run", "long",
                "aunt", "unit", "cool", "push", "wrap", "hide", "bind", "loud", "band", "fax", "wood", "soul", "fork",
                "raid", "disk", "test", "keep", "frog", "hook", "cake", "read", "bury", "wolf", "sale", "rice", "rack",
                "ride", "air", "food", "gold", "bank", "have", "slam", "fold", "fire", "stir", "hate", "note", "fuss",
                "lip", "last", "core", "kill", "sand", "edge", "bark", "help", "moon", "fail", "fine", "dash", "TRUE",
                "snap", "road", "meet", "game", "well", "glue", "half", "fast", "drug", "lump", "AIDS", "blow", "soar",
                "cafe", "pot", "hut", "body", "horn", "want", "rise", "dark", "herb", "nap", "code", "heel", "call",
                "path", "baby", "slab", "goat", "take", "exit", "roar" };
        Random indexGen = new Random();

        return words[indexGen.nextInt(199)] + "-" + words[indexGen.nextInt(199)] + "-" + words[indexGen.nextInt(199)];
    }

    public static String upload(User user, JSONArray files, String dumpID) {
        URL url;
        HttpURLConnection con;

        try {
            url = new URL("https://pasteyboi.appspot.com/dump");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            JSONObject dump = new JSONObject();
            dump.put("userID", user.getUserID());
            dump.put("password", user.getHashedPassword());
            dump.put("dumpID", dumpID);
            dump.put("contents", files);

            String json = dump.toJSONString();

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }

            return dumpID;

        } catch (IOException e) {
            System.err.println(e);
            System.exit(-1);
        }

        return "Failed";
    }

    public static JSONArray getUserDumps(User user) {
        URL url;
        HttpURLConnection con;
        try {
            url = new URL("https://pasteyboi.appspot.com/getUserDumps?userID=" + user.getUserID());
            con = (HttpURLConnection) url.openConnection();
		    con.setRequestMethod("GET");
		    int responseCode = con.getResponseCode();
		    System.out.println("GET Response Code :: " + responseCode);
		    if (responseCode == HttpURLConnection.HTTP_OK) { // success
			    BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			    String inputLine;
			    StringBuffer response = new StringBuffer();

			    while ((inputLine = in.readLine()) != null) {
			    	response.append(inputLine);
			    }
			    in.close();
                return (JSONArray) (new JSONParser()).parse(response.toString());

            } else {
                System.out.println("GET request not worked");
            }

        } catch (IOException | ParseException e){
            System.err.println(e);
            System.exit(1);
        }

        return new JSONArray();
    }

    public static JSONArray genJSON(ArrayList<String> files) {
        JSONArray jsonArray = new JSONArray();
        int i = 0;

        for (String file : files) {
            JSONObject obj = new JSONObject();
            obj.put("fileIndex", i);
            obj.put("fileName", file);

            Scanner sc;
            String body = "";
            try {
                sc = new Scanner(new File(file));

                while (sc.hasNextLine()){
                    body += sc.nextLine() + "\n";
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            obj.put("body", body);
            jsonArray.add(obj);

            i ++;
        }

        return jsonArray;
    }

    public static void main(String[] args) {
        User u = new User("joerourke", "hello");

        JSONArray files = new JSONArray();
        JSONObject one = new JSONObject();
        one.put("fileIndex", 0);
        one.put("fileName", "file.txt");
        one.put("body", "new");
        files.add(one);

//        upload(u, files);

//        download(u, "jet-read-herd");

        getUserDumps(u);
    }
}