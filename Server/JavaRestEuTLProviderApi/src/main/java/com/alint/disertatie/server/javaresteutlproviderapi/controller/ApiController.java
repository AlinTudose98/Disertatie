package com.alint.disertatie.server.javaresteutlproviderapi.controller;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

@RestController
@Log4j2
@RequestMapping("/api")
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

    @RequestMapping("/lotl")
    public String lotl() throws IOException, ParserConfigurationException, SAXException {

        XmlMapper mapper = new XmlMapper();
        URL url = new URL(env.getProperty("dss.europa.tl.lotl_url"));
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        Document doc = db.parse(url.openStream());
        System.out.println(mapper.writeValueAsString(doc));

        if(authMode.equalsIgnoreCase("server") || authMode.equalsIgnoreCase("both"))
        {
            String filename = "../temp/lotl_" + System.currentTimeMillis() + ".xml";
            File tmpFile = new File(filename);
            if(!tmpFile.createNewFile())
                throw new IOException();

            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(mapper.writeValueAsString(doc));
            writer.close();
            job.onlineRefresh();
            SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(new FileDocument(filename));

            validator.setCertificateVerifier(certificateVerifier);

            Reports reports = validator.validateDocument();
            SimpleReport simpleReport = reports.getSimpleReport();

            Indication indication = simpleReport.getIndication(simpleReport.getFirstSignatureId());
            if(!tmpFile.delete())
                throw new IOException();
            if(!indication.getUri().equals(Indication.TOTAL_PASSED) && !indication.getUri().equals(Indication.PASSED))
            {
                return mapper.writeValueAsString(simpleReport);
            }


        }

        return mapper.writeValueAsString(doc);
    }
}
