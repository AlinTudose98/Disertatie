package com.alint.disertatie.javaeutlclient.util;

import com.alint.disertatie.javaeutlclient.model.entity.ListOfTrustedLists;

import com.alint.disertatie.javaeutlclient.model.message.LotlResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerRetriever {
    private Environment env;

    public ServerRetriever() throws IOException {
        env = EnvironmentFactory.getNewApplicationPropertiesInstance();
    }

    public ServerRetriever(String serverPropertiesFilePath) throws IOException {
        env = EnvironmentFactory.getNewInstance(serverPropertiesFilePath);
    }

    public ListOfTrustedLists getListOfTrustedLists() throws IOException {
        URL url = new URL(env.getProperties().getProperty("server.lotl.url"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type","application/json");

        int status = conn.getResponseCode();
        if(status > 299)
            throw new IOException("Server did not handle the request");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String response;
        StringBuilder builder = new StringBuilder();
        while((response = in.readLine()) != null)
            builder.append(response);
        in.close();
        conn.disconnect();

        response = builder.toString();

        ObjectMapper mapper = new ObjectMapper();

        LotlResponse lotlResponse = mapper.readValue(response, LotlResponse.class);

        return lotlResponse.getListOfTrustedLists();
    }
}
