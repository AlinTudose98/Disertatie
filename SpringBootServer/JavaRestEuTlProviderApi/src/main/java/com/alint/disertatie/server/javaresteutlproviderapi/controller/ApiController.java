package com.alint.disertatie.server.javaresteutlproviderapi.controller;

import com.alint.disertatie.server.javaresteutlproviderapi.message.ResponseMessage;
import com.alint.disertatie.server.javaresteutlproviderapi.util.Util;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

@RestController
@Log4j2
@RequestMapping("/")
public class ApiController {

    private final Environment env;
    private final String authMode;
    private final String authForce;
    private final CertificateVerifier certificateVerifier;
    private final TLValidationJob job;

    @Autowired
    public ApiController(Environment env, CertificateVerifier certificateVerifier, TLValidationJob job) {
        this.env = env;
        this.certificateVerifier = certificateVerifier;
        this.job = job;
        this.authMode = env.getProperty("java.utils.operation.auth.mode");
        this.authForce = env.getProperty("java.utils.operation.auth.force");
    }

    @RequestMapping(value = "/lotl", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage lotl() throws IOException, ParserConfigurationException, SAXException {


        String response = Util.getResponseFromUrl(env.getProperty("dss.europa.tl.lotl_url"));

        ResponseMessage responseMessage = new ResponseMessage(0, null, authMode, "FILE", response);

        if (authMode.equalsIgnoreCase("server") || authMode.equalsIgnoreCase("both")) {
            String filename = "../temp/lotl_" + System.currentTimeMillis() + ".xml";
            File tmpFile = new File(filename);
            if (!tmpFile.createNewFile())
                throw new IOException();

            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(response);
            writer.close();

            TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();
            job.setTrustedListCertificateSource(trustedListsCertificateSource);
            job.onlineRefresh();
            certificateVerifier.setTrustedCertSources(trustedListsCertificateSource);

            SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(new FileDocument(filename));

            validator.setCertificateVerifier(certificateVerifier);

            Reports reports = validator.validateDocument();
            SimpleReport simpleReport = reports.getSimpleReport();

            Indication indication = simpleReport.getIndication(simpleReport.getFirstSignatureId());
            if (!tmpFile.delete())
                throw new IOException();

            if (!indication.equals(Indication.TOTAL_PASSED) && !indication.equals(Indication.PASSED))
                responseMessage.setStatus(1);
            else
                responseMessage.setStatus(0);

            responseMessage.setIndication(indication.name());
        }

        return responseMessage;
    }

    @RequestMapping(value = "/tl/{countryCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage tl(@PathVariable String countryCode) throws IOException, ParserConfigurationException, SAXException {

        ResponseMessage responseMessage = new ResponseMessage(0,null,authMode,"FILE",null);
        String response = Util.getResponseFromUrl(env.getProperty("dss.europa.tl.lotl_url"));
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream inputStream = IOUtils.toInputStream(response);
        Document doc = db.parse(inputStream);
        doc.getDocumentElement().normalize();
        inputStream.close();

        Element schemeInformation = (Element) doc.getElementsByTagName("SchemeInformation").item(0);
        Element pointersToOtherTSL = (Element) schemeInformation.getElementsByTagName("PointersToOtherTSL").item(0);
        NodeList otherTslPointers = pointersToOtherTSL.getChildNodes();

        for(int i = 0; i < otherTslPointers.getLength(); i++)
        {
            if (otherTslPointers.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element pointer = (Element) otherTslPointers.item(i);
            Element additionalInformation = (Element) pointer.getElementsByTagName("AdditionalInformation").item(0);
            NodeList otherInformationList = additionalInformation.getChildNodes();
            for (int j = 0; j < otherInformationList.getLength();j++)
            {
                if(otherInformationList.item(j).getNodeType() != Node.ELEMENT_NODE)
                    continue;
                Element otherInformation = (Element) otherInformationList.item(j);
                Element schemeTerritory = (Element) otherInformation.getElementsByTagName("SchemeTerritory").item(0);

                if(schemeTerritory == null)
                    continue;

                if(countryCode.equalsIgnoreCase(schemeTerritory.getFirstChild().getNodeValue()))
                {
                    response =  Util.getResponseFromUrl(pointer.getElementsByTagName("TSLLocation")
                            .item(0).getFirstChild().getNodeValue());
                    responseMessage.setResponseBody(response);


                    return responseMessage;
                }
                else
                    break;
            }

        }

        responseMessage.setStatus(404);
        responseMessage.setResponseBody("Country code not found: " + countryCode);
        return responseMessage;
    }
}