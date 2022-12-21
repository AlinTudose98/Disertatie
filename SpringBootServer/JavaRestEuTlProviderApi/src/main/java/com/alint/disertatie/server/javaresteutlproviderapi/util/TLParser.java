package com.alint.disertatie.server.javaresteutlproviderapi.util;

import com.alint.disertatie.server.javaresteutlproviderapi.entity.ListOfTrustedLists;
import com.alint.disertatie.server.javaresteutlproviderapi.entity.OtherTSLPointer;
import com.alint.disertatie.server.javaresteutlproviderapi.entity.TrustedList;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.MimeType;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.TSLType;
import com.alint.disertatie.server.javaresteutlproviderapi.exception.BadRequestException;
import com.alint.disertatie.server.javaresteutlproviderapi.exception.CountryNotFoundException;
import com.alint.disertatie.server.javaresteutlproviderapi.exception.CustomException;
import com.alint.disertatie.server.javaresteutlproviderapi.exception.TrustedListNotParsedException;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.jaxb.object.Message;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class TLParser {

    private final CertificateVerifier certificateVerifier;
    private final MemoryCell memoryCell;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault());
    private final long expireTime;


    @Autowired
    public TLParser(CertificateVerifier certificateVerifier, MemoryCell memoryCell, Environment env) {
        this.certificateVerifier = certificateVerifier;
        this.memoryCell = memoryCell;
        this.expireTime = Long.parseLong(env.getProperty("dss.europa.tl.parser.run_interval_s","600"));
    }

    public TrustedList getTL(String countryCode) throws BadRequestException, CountryNotFoundException {
        try{
            if(countryCode.equalsIgnoreCase("EU"))
                throw new BadRequestException("List of the lists should be accessed with the /lotl endpoint.");

            TrustedList trustedList = memoryCell.getTL(countryCode);
            if(System.currentTimeMillis() -  Instant.from(formatter.parse(trustedList.getLastUpdated())).toEpochMilli() > expireTime * 1000) {
                throw new TrustedListNotParsedException("Trusted list information has expired.");
            }
            return trustedList;
        }
        catch (TrustedListNotParsedException e) {
            log.info(e.getMessage());
            return this.addTrustedList(countryCode);
        }
    }

    private TrustedList verifyTrustedList(String tlXmlSource) throws IOException {

        SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(
                new InMemoryDocument(tlXmlSource.getBytes(StandardCharsets.UTF_8))
        );
        validator.setCertificateVerifier(certificateVerifier);

        Reports reports = validator.validateDocument();
        SimpleReport simpleReport = reports.getSimpleReport();
        List<Message> warnings = simpleReport.getQualificationWarnings(simpleReport.getFirstSignatureId());
        warnings.addAll(simpleReport.getAdESValidationWarnings(simpleReport.getFirstSignatureId()));
        Indication indication = simpleReport.getIndication(simpleReport.getFirstSignatureId());
        SubIndication subIndication = simpleReport.getSubIndication(simpleReport.getFirstSignatureId());

        TrustedList trustedList = new TrustedList();
        trustedList.setIndication(indication.name());

        if(subIndication!=null)
            trustedList.setSubIndication(subIndication.name());
        else
            trustedList.setSubIndication(null);

        trustedList.setWarnings(warnings);

        return trustedList;
    }

    private TrustedList parseTrustedList(TrustedList trustedList, String tlXmlSource) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream inputStream = IOUtils.toInputStream(tlXmlSource,StandardCharsets.UTF_8);
        Document doc = db.parse(inputStream);
        doc.getDocumentElement().normalize();
        inputStream.close();

        Element schemeInformation = (Element) doc.getElementsByTagName("SchemeInformation").item(0);

        Element distributionPoint = (Element) schemeInformation.getElementsByTagName("DistributionPoints").item(0);
        if(distributionPoint == null)
            log.warn("Found trusted list without distribution point!");
        if(distributionPoint != null) {
            distributionPoint = (Element) distributionPoint.getElementsByTagName("URI").item(0);
            trustedList.setDistributionPoint(distributionPoint.getTextContent());
        }

        Element schemeTerritory = (Element) schemeInformation.getElementsByTagName("SchemeTerritory").item(0);
        Element listIssueDateTime = (Element) schemeInformation.getElementsByTagName("ListIssueDateTime").item(0);
        Element historicalInformationPeriod = (Element) schemeInformation.getElementsByTagName("HistoricalInformationPeriod").item(0);
        Element nextUpdate = (Element) schemeInformation.getElementsByTagName("NextUpdate").item(0);
        nextUpdate = (Element) nextUpdate.getElementsByTagName("dateTime").item(0);
        if(nextUpdate == null) {
            log.warn("Found trusted list with no next update field.");
            trustedList.setNextUpdate(null);
        }
        else {
            trustedList.setNextUpdate(nextUpdate.getTextContent());
        }


        trustedList.setSchemeTerritory(schemeTerritory.getTextContent());
        trustedList.setListIssueDateTime(listIssueDateTime.getTextContent());
        trustedList.setHistoricalInformationPeriod(Integer.parseInt(historicalInformationPeriod.getTextContent()));
        trustedList.setTslType(TSLType.EUGeneric);

        Element pointersToOtherTsl = (Element) schemeInformation.getElementsByTagName("PointersToOtherTSL").item(0);
        trustedList.setPointersToOtherTSL(Util.parsePointersToOtherTsl(pointersToOtherTsl));

        Element trustServiceProvidersList = (Element) doc.getElementsByTagName("TrustServiceProviderList").item(0);
        trustedList.setTrustServiceProviders(Util.parseTrustServiceProviders(trustServiceProvidersList));

        return trustedList;
    }

    public synchronized TrustedList addTrustedList(String countryCode) {
        try {
            log.info("Downloading trusted list for country code: " + countryCode);
            ListOfTrustedLists lotl = memoryCell.getLotl();
            while(lotl.getLastUpdated() == null ||
                    System.currentTimeMillis() -  Instant.from(formatter.parse(lotl.getLastUpdated())).toEpochMilli() > expireTime * 1000) {
                Thread.sleep(5000);
                log.warn("LOTL expired or not yet parsed... waiting");
                lotl = memoryCell.getLotl();
            }

            OtherTSLPointer desiredPointer = lotl.getCCTSLPointer(countryCode);
            if(desiredPointer == null) {
                log.warn("Country iso code is invalid: " + countryCode + ". No such code exists in LOTL");
                throw new CountryNotFoundException("Country not found for ISO CODE: " + countryCode);
            }

            String tlXmlSource = Util.getResponseFromUrl(desiredPointer.getTslLocation());
            TrustedList trustedList = this.verifyTrustedList(tlXmlSource);
            log.info("Verified trusted list for country code " + countryCode);
            this.parseTrustedList(trustedList, tlXmlSource);
            log.info("Parsed trusted list for country code " + countryCode);
            trustedList.setLastUpdated(formatter.format(LocalDateTime.now()));
            memoryCell.addTL(trustedList);
            log.info("Added trusted list into memory cache");
            return trustedList;
        }  catch (InterruptedException | IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
