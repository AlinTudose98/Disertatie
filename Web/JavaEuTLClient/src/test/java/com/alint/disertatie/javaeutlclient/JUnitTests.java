package com.alint.disertatie.javaeutlclient;


import com.alint.disertatie.javaeutlclient.model.entity.ListOfTrustedLists;
import com.alint.disertatie.javaeutlclient.util.Environment;
import com.alint.disertatie.javaeutlclient.util.EnvironmentFactory;
import com.alint.disertatie.javaeutlclient.util.ServerRetriever;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JUnitTests {
    @Test
    void contextLoads() {}

    @Test
    void propertiesGetParsedCorrectly() throws IOException {
        Environment env = EnvironmentFactory.getNewApplicationPropertiesInstance();
        assertEquals(env.getProperties().getProperty("server.lotl.url"),"https://eutlservice:8443/api/lotl");
        assertEquals(env.getProperties().getProperty("server.tl.url"),"https://eutlservice:8443/api/tl/");
    }

    @Test
    void appRetrievesLOTLFromServer() throws IOException {
        ServerRetriever retriever = new ServerRetriever();

        ListOfTrustedLists lotl = retriever.getListOfTrustedLists();
        System.out.println(lotl);
    }
}