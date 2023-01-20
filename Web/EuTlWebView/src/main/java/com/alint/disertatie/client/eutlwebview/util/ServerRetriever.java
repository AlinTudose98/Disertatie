package com.alint.disertatie.client.eutlwebview.util;

import com.alint.disertatie.client.eutlwebview.model.entity.ListOfTrustedLists;
import com.alint.disertatie.client.eutlwebview.model.entity.TrustedList;
import com.alint.disertatie.client.eutlwebview.model.message.LotlResponse;
import com.alint.disertatie.client.eutlwebview.model.message.TlResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

@Component
public class ServerRetriever {
    private final Environment env;

    @Autowired
    public ServerRetriever(Environment env) {
        this.env = env;
    }

    public ListOfTrustedLists getListOfTrustedLists() throws IOException {
        URL url = new URL(env.getProperty("server.lotl.url","https://eutlservice:8443/api/lotl"));
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

    public TrustedList getTrustedList(String countryCode) throws IOException {
        URL url = new URL(env.getProperty("server.tl.url","https://eutlservice:8443/api/tl/") + countryCode);
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

        TlResponse tlResponse = mapper.readValue(response, TlResponse.class);
        return tlResponse.getTrustedList();
    }
}
