package com.pasteyboi.client;

public class CliClient {
    static String username, password;

    public static void main(String[] args){
        for (int i = 0; i < args.length; i ++){
            String curr = args[i];

            if (curr.equals("--upload")){
                System.out.print(args[i + 1]);
                i ++;
            } else if (curr.equals("--download")){
                String id = args[i + 1];

                i ++;
            } else if (curr.equals("--username")){
                username = args[i + 1];
                i ++;
            } else if (curr.equals("--password")){
                password = args[i + 1];
                i ++;
            } else if (curr.equals("--list")){
                System.out.println(args);
            } else {
                System.err.println("Invalid argument");
            }
        }
    }
}