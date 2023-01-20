package com.alint.disertatie.server.javaresteutlproviderapi;

import com.alint.disertatie.server.javaresteutlproviderapi.entity.ListOfTrustedLists;
import com.alint.disertatie.server.javaresteutlproviderapi.entity.OtherTSLPointer;
import com.alint.disertatie.server.javaresteutlproviderapi.entity.TrustServiceProvider;
import com.alint.disertatie.server.javaresteutlproviderapi.util.EuTLParser;
import com.alint.disertatie.server.javaresteutlproviderapi.util.EuTLValidator;
import com.alint.disertatie.server.javaresteutlproviderapi.util.Util;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class JavaRestEuTlProviderApiApplicationTests {

    private final Environment env;
    private final EuTLParser parser;
    private final EuTLValidator validator;

    @Autowired
    public JavaRestEuTlProviderApiApplicationTests(Environment env, EuTLParser parser, EuTLValidator validator) {
        this.env = env;
        this.parser = parser;
        this.validator = validator;
    }

    @Test
    void contextLoads() {
    }

    @Test
    void LotlGetsParsed() throws IOException {
        Thread myValThread = new Thread(validator);
        myValThread.start();
        Thread myThread = new Thread(parser);
        myThread.start();

        String lotlXmlSource = Util.getResponseFromUrl(env.getProperty("dss.europa.tl.lotl_url"));
        ListOfTrustedLists listOfTrustedLists = parser.getListOfTrustedLists();
        for (OtherTSLPointer pointer: listOfTrustedLists.getPointersToOtherTsl()) {
            System.out.println("curl --insecure https://eutlservice:8443/api/tl/" + pointer.getSchemeTerritory());
        }
    }

    @Test
    void TSPInformationGetsPopulatedWell() throws Exception {
        String ATTlXmlSource = Util.getResponseFromUrl("https://www.signatur.rtr.at/currenttl.xml");

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream inputStream = IOUtils.toInputStream(ATTlXmlSource, StandardCharsets.UTF_8);
        Document doc = db.parse(inputStream);
        doc.getDocumentElement().normalize();
        inputStream.close();

        Element trustServiceProvidersList = (Element) doc.getElementsByTagName("TrustServiceProviderList").item(0);
        Element trustServiceProvider = (Element) trustServiceProvidersList.getElementsByTagName("TrustServiceProvider").item(0);
        Element tspInformation = (Element) trustServiceProvider.getElementsByTagName("TSPInformation").item(0);

        TrustServiceProvider tsp = new TrustServiceProvider();
        Util.parseTSPInformation(tspInformation, tsp, false);

        assertEquals("A-Trust Gesellschaft für Sicherheitssysteme im elektronischen Datenverkehr GmbH",tsp.getName());
        assertEquals("A-Trust Ges. für Sicherheitssysteme im elektr. Datenverkehr GmbH", tsp.getTradeName());
        assertEquals(1, tsp.getPostalAddresses().size());
        assertEquals(3,tsp.getElectronicAddresses().size());
    }

    @Test
    void TSPGetsParsedWell() throws Exception{
        String ATTlXmlSource = Util.getResponseFromUrl("https://www.signatur.rtr.at/currenttl.xml");

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream inputStream = IOUtils.toInputStream(ATTlXmlSource, StandardCharsets.UTF_8);
        Document doc = db.parse(inputStream);
        doc.getDocumentElement().normalize();
        inputStream.close();

        Element trustServiceProvidersList = (Element) doc.getElementsByTagName("TrustServiceProviderList").item(0);
        Element trustServiceProvider = (Element) trustServiceProvidersList.getElementsByTagName("TrustServiceProvider").item(0);
        Element tspServices = (Element) trustServiceProvider.getElementsByTagName("TSPServices").item(0);


        TrustServiceProvider tsp = new TrustServiceProvider();
        Util.parseTSPInformation(trustServiceProvider, tsp,false);
        tsp.setTrustServices(Util.parseTrustServices(tspServices,false));

        System.out.println(tsp);
    }
}
