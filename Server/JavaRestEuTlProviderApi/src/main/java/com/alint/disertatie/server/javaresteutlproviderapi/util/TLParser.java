package com.alint.disertatie.server.javaresteutlproviderapi.util;

import com.alint.disertatie.server.javaresteutlproviderapi.entity.*;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.TSLType;
import com.alint.disertatie.server.javaresteutlproviderapi.exception.BadRequestException;
import com.alint.disertatie.server.javaresteutlproviderapi.exception.CountryNotFoundException;
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

        trustedList.setWarnings(new ArrayList<>());
        for(Message iter: warnings) {
          trustedList.getWarnings().add(new CustomMessage(iter.getKey(),iter.getValue()));
        }
        //trustedList.setWarnings(warnings);

        return trustedList;
    }

    private TrustedList parseTrustedList(TrustedList trustedList, String tlXmlSource,boolean nsMode) throws ParserConfigurationException, IOException, SAXException {
        String ns = "";
        if (nsMode)
            ns = "tsl:";

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream inputStream = IOUtils.toInputStream(tlXmlSource,StandardCharsets.UTF_8);
        Document doc = db.parse(inputStream);
        doc.getDocumentElement().normalize();
        inputStream.close();

        Element schemeInformation = (Element) doc.getElementsByTagName(ns + "SchemeInformation").item(0);

        Element distributionPoint = (Element) schemeInformation.getElementsByTagName(ns + "DistributionPoints").item(0);
        if(distributionPoint == null)
            log.warn("Found trusted list without distribution point!");
        if(distributionPoint != null) {
            distributionPoint = (Element) distributionPoint.getElementsByTagName(ns + "URI").item(0);
            trustedList.setDistributionPoint(distributionPoint.getTextContent());
        }

        Element schemeTerritory = (Element) schemeInformation.getElementsByTagName(ns + "SchemeTerritory").item(0);
        Element listIssueDateTime = (Element) schemeInformation.getElementsByTagName(ns + "ListIssueDateTime").item(0);
        Element historicalInformationPeriod = (Element) schemeInformation.getElementsByTagName(ns + "HistoricalInformationPeriod").item(0);
        Element nextUpdate = (Element) schemeInformation.getElementsByTagName(ns + "NextUpdate").item(0);
        nextUpdate = (Element) nextUpdate.getElementsByTagName(ns + "dateTime").item(0);
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

        Element tslVersionIdentifier = (Element) doc.getElementsByTagName(ns + "TSLVersionIdentifier").item(0);
        trustedList.setTslVersionIdentifier(Integer.parseInt(tslVersionIdentifier.getTextContent()));

        Element tslSequenceNumber = (Element) doc.getElementsByTagName(ns + "TSLSequenceNumber").item(0);
        trustedList.setTslSequenceNumber(Integer.parseInt(tslSequenceNumber.getTextContent()));

        Element schemeOperatorName = (Element) doc.getElementsByTagName(ns + "SchemeOperatorName").item(0);
        NodeList names = schemeOperatorName.getElementsByTagName(ns + "Name");
        for(int i=0; i< names.getLength(); i++) {
            if(names.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element name = (Element) names.item(i);
            if(name.getAttribute("xml:lang").equals("en")) {
                trustedList.setSchemeOperatorName(name.getTextContent());
                break;
            }
        }

        Element schemeOperatorAddresses = (Element) doc.getElementsByTagName(ns + "SchemeOperatorAddress").item(0);
        Element schemeOperatorPostalAddresses = (Element) schemeOperatorAddresses.getElementsByTagName(ns + "PostalAddresses").item(0);
        Element SchemeOperatorElectronicAddresses = (Element) schemeOperatorAddresses.getElementsByTagName(ns + "ElectronicAddress").item(0);

        List<PostalAddress> postalAddresses = new ArrayList<>();
        List<String> emailAddresses = new ArrayList<>();

        NodeList tspPostalAddressesList = schemeOperatorPostalAddresses.getElementsByTagName(ns + "PostalAddress");
        for (int i = 0; i < tspPostalAddressesList.getLength(); i++) {
            if (tspPostalAddressesList.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element address = (Element) tspPostalAddressesList.item(i);
            if (!address.getAttribute("xml:lang").equalsIgnoreCase("en"))
                continue;
            PostalAddress postalAddress = new PostalAddress();
            postalAddress.setPostalCode(address.getElementsByTagName(ns + "StreetAddress").item(0).getTextContent());
            postalAddress.setLocality(address.getElementsByTagName(ns + "Locality").item(0).getTextContent());
            postalAddress.setCountryName(address.getElementsByTagName(ns + "CountryName").item(0).getTextContent());
            postalAddress.setStreetAddress(address.getElementsByTagName(ns + "StreetAddress").item(0).getTextContent());

            postalAddresses.add(postalAddress);
        }

        NodeList tspElectronicAddressesList = SchemeOperatorElectronicAddresses.getElementsByTagName(ns + "URI");
        for(int j=0; j< tspElectronicAddressesList.getLength(); j++) {
            if (tspElectronicAddressesList.item(j).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element uri = (Element) tspElectronicAddressesList.item(j);
            emailAddresses.add(uri.getTextContent());
        }

        trustedList.setPostalAddresses(postalAddresses);
        trustedList.setElectronicAddresses(emailAddresses);

        Element schemeName = (Element) doc.getElementsByTagName(ns + "SchemeName").item(0);
        names = schemeName.getElementsByTagName(ns + "Name");
        for(int i=0; i< names.getLength(); i++) {
            if(names.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element name = (Element) names.item(i);
            if(name.getAttribute("xml:lang").equals("en")) {
                trustedList.setSchemeName(name.getTextContent());
                break;
            }
        }

        trustedList.setSchemeInformationURI(new ArrayList<>());
        Element schemeInformationUri = (Element) doc.getElementsByTagName(ns + "SchemeInformationURI").item(0);
        NodeList uris = schemeInformationUri.getElementsByTagName("URI");
        for(int i=0; i< uris.getLength(); i++) {
            if(uris.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element uri = (Element) uris.item(i);
            if(uri.getAttribute("xml:lang").equals("en")) {
                trustedList.getSchemeInformationURI().add(uri.getTextContent());
            }
        }

        Element statusDeterminationApproach = (Element) doc.getElementsByTagName(ns + "StatusDeterminationApproach").item(0);
        trustedList.setStatusDeterminationApproach(statusDeterminationApproach.getTextContent());

        Element schemeTypeCommunityRules = (Element) doc.getElementsByTagName(ns + "SchemeTypeCommunityRules").item(0);
        uris = schemeTypeCommunityRules.getElementsByTagName(ns + "URI");
        for(int i=0; i< uris.getLength(); i++) {
            if(uris.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element uri = (Element) uris.item(i);
            if(uri.getAttribute("xml:lang").equals("en")) {
                trustedList.setSchemeTypeCommunityRules(uri.getTextContent());
                break;
            }
        }

        Element policyOrLegalNotice = (Element) doc.getElementsByTagName(ns + "PolicyOrLegalNotice").item(0);
        NodeList tslLegalNotices = policyOrLegalNotice.getElementsByTagName(ns + "TSLLegalNotice");
        for(int i=0; i< tslLegalNotices.getLength(); i++) {
            if(tslLegalNotices.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element tslLegalNotice = (Element) tslLegalNotices.item(i);
            if(tslLegalNotice.getAttribute("xml:lang").equals("en")) {
                trustedList.setPolicyOrLegalNotice(tslLegalNotice.getTextContent());
                break;
            }
        }



        Element pointersToOtherTsl = (Element) schemeInformation.getElementsByTagName(ns + "PointersToOtherTSL").item(0);
        trustedList.setPointersToOtherTSL(Util.parsePointersToOtherTsl(pointersToOtherTsl, nsMode));

        Element trustServiceProvidersList = (Element) doc.getElementsByTagName(ns + "TrustServiceProviderList").item(0);
        trustedList.setTrustServiceProviders(Util.parseTrustServiceProviders(trustServiceProvidersList, nsMode));

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

            boolean nsMode = false;
            if(countryCode.equalsIgnoreCase("HU"))
                nsMode=true;

            String tlXmlSource = Util.getResponseFromUrl(desiredPointer.getTslLocation());
            TrustedList trustedList = this.verifyTrustedList(tlXmlSource);
            log.info("Verified trusted list for country code " + countryCode);
            this.parseTrustedList(trustedList, tlXmlSource, nsMode);
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
