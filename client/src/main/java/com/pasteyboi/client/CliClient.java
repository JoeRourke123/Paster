package com.pasteyboi.client;

import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class CliClient {
    static String username, password;

    static User user = new User();

    public static void main(String[] args){
        for (int i = 0; i < args.length; i ++){
            String curr = args[i];

            if (curr.equals("--upload")){
                boolean flag = true;
                ArrayList<String> files = new ArrayList<String>();
                while (flag){
                    if (i < args.length){
                        if (args[i + 1].substring(0, 2).equals("--")){
                            flag = false; 
                        } else {
                            files.add(args[i + 1]);
                            i ++;
                        }
                    } else {
                        flag = false;
                    }
                }
                System.err.printf("Uploading file(s); %s%n", files);

                JSONArray jsonArray = Transfer.genJSON(files);

                System.err.printf("Dump ID: %s%n", Transfer.upload(user, jsonArray, Transfer.generateDumpID()));
                
            } else if (curr.equals("--download")){
                String id = args[i + 1];


                //user = new User(username, password);
                JSONObject dump = Transfer.download(user, id);

                for(Object file : (JSONArray) dump.get("contents")) {
                    System.out.printf("Filename: %s, Index: %s%n", ((JSONObject) file).get("fileName"), ((JSONObject) file).get("fileIndex"));
                    System.out.println(((JSONObject) file).get("body"));
                }

                i ++;
            } else if (curr.equals("--username")){
                user.setUsername(args[i + 1]);
                i ++;
            } else if (curr.equals("--password")) {
                user.setPassword(args[i + 1]);

                user = new User(user.getUsername(), user.getHashedPassword());

                System.out.println(user.getAuth());
                i++;
            } else if (curr.equals("--dumps")) {
                JSONArray dumps = Transfer.getUserDumps(user);
                Scanner scan = new Scanner(System.in);
                String in = "";

                while(!in.equals("-1")) {
                    for (Object obj : dumps) {
                        System.out.println(((JSONObject)obj).get("dumpID"));
                    }
                }
            } else if (curr.equals("--list")){
                System.out.println(args);
            } else if (curr.equals("--help")){
                System.err.println("\nPasteyBoi 0.1");
                System.err.println();
                System.err.println("Username & password need to be specified before other arguments");
                System.err.println();
                System.err.println("Options:");
                System.err.println("    --username [USERNAME]");
                System.err.println("    --password [PASSWORD]");
                System.err.println("    --download [DUMPID]");
                System.err.println("    --upload [FILE]");
                //System.err.println("");
                //System.err.println("");
                    
            } else {
                System.err.println("Invalid argument, type --help or -h for help");
            }
        }
    }
}