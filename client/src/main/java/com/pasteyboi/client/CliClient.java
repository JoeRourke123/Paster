package com.pasteyboi.client;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class CliClient {
    static String username, password;

    public static void main(String[] args){
        for (int i = 0; i < args.length; i ++){
            String curr = args[i];

            if (curr.equals("--upload")){
                boolean flag = true;
                ArrayList<String> files = new ArrayList<String>();
                while (flag){
                    if (args[i + 1].substring(0, 2).equals("--")){
                        flag = false; 
                    } else {
                        files.add(args[i + 1]);
                        i ++;
                    }
                }
                System.out.printf("Uploading file(s); %s", files);
                
            } else if (curr.equals("--download")){
                String id = args[i + 1];

                User user = new User(username, password);
                JSONObject dump = Transfer.download(user, id);

                for(Object file : (JSONArray) dump.get("contents")) {
                    System.out.println(((JSONObject) file).get("body"));
                }

                i ++;
            } else if (curr.equals("--username")){
                username = args[i + 1];
                i ++;
            } else if (curr.equals("--password")){
                password = args[i + 1];
                i ++;
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