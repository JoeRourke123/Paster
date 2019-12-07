package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

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

                download(id);
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

    public static void download(String id){
        URL url;
        HttpURLConnection con;
        try {
            url = new URL("https://httpbin.org/get");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

                    
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        } catch (IOException e){
            System.err.println(e);
            System.exit(1);
        }

    }
}