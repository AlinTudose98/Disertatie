package com.alint.disertatie.server.javaresteutlproviderapi.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Util {
    public static String getResponseFromUrl(String urlString) throws IOException {

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            response.append(line);
            response.append("\n");
        }
        response.deleteCharAt(response.lastIndexOf("\n"));
        reader.close();

        return  response.toString();
    }

    public static String getFileContent(String filename) throws IOException {

        File file = new File(filename);

        StringBuilder buffer = new StringBuilder();

        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            buffer.append(line);
            buffer.append("\n");
        }
        scanner.close();

        return buffer.toString();
    }
}
