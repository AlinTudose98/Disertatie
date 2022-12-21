package com.alint.disertatie.server.javaresteutlproviderapi;

import com.alint.disertatie.server.javaresteutlproviderapi.entity.TrustServiceProvider;
import com.alint.disertatie.server.javaresteutlproviderapi.util.Util;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class JavaRestEuTlProviderApiApplicationTests {

    @Test
    void contextLoads() {
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
        Util.parseTSPInformation(tspInformation, tsp);

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
        Util.parseTSPInformation(trustServiceProvider, tsp);
        tsp.setTrustServices(Util.parseTrustServices(tspServices));

        System.out.println(tsp);
    }
}
