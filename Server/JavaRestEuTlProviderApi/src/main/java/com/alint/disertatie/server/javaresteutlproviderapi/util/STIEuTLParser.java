package com.alint.disertatie.server.javaresteutlproviderapi.util;


import com.alint.disertatie.server.javaresteutlproviderapi.entity.STICustomMessage;
import com.alint.disertatie.server.javaresteutlproviderapi.entity.STIListOfTrustedLists;
import com.alint.disertatie.server.javaresteutlproviderapi.entity.STIPostalAddress;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.STITSLType;
import com.google.common.util.concurrent.Monitor;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.jaxb.object.Message;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
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
@Getter
@Setter
@Log4j2
public class STIEuTLParser implements Runnable {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault());
    private final Monitor parsingMutex;
    private final Monitor validationMutex;
    private final Environment env;
    private boolean doStop = false;
    private ApplicationContext applicationContext;
    private STIMemoryCell memoryCell;
    private long runInterval;
    private long lastVerifyTimestamp;
    private CertificateVerifier certificateVerifier;

    @Autowired
    public STIEuTLParser(ApplicationContext applicationContext, Environment env, @Qualifier("tlParserMutex") Monitor parsingMutex,
                         @Qualifier("tlValidationMutex") Monitor validationMutex, STIMemoryCell memoryCell) {
        this.applicationContext = applicationContext;
        this.env = env;
        this.parsingMutex = parsingMutex;
        this.validationMutex = validationMutex;

        this.lastVerifyTimestamp = 0;
        this.memoryCell = memoryCell;
    }

    @PostConstruct
    private void configure() {
        this.runInterval = Long.parseLong(
                env.getProperty("dss.europa.tl.parser.run_interval_s", "600")
        );
        this.certificateVerifier = this.getApplicationContext()
                .getBean("STIEuTLValidator", STIEuTLValidator.class).getCertificateVerifier();
    }


    public synchronized void doStop() {
        this.doStop = true;
    }

    public synchronized boolean keepRunning() {
        return !this.doStop;
    }

    private STIListOfTrustedLists verifyLotl(String lotlXmlSource) {
        STIListOfTrustedLists listOfTrustedLists = new STIListOfTrustedLists();

        SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(new InMemoryDocument(
                lotlXmlSource.getBytes(StandardCharsets.UTF_8)
        ));
        validator.setCertificateVerifier(certificateVerifier);

        Reports reports = validator.validateDocument();
        SimpleReport simpleReport = reports.getSimpleReport();
        List<Message> warnings = simpleReport.getQualificationWarnings(simpleReport.getFirstSignatureId());
        warnings.addAll(simpleReport.getAdESValidationWarnings(simpleReport.getFirstSignatureId()));
        Indication indication = simpleReport.getIndication(simpleReport.getFirstSignatureId());
        SubIndication subIndication = simpleReport.getSubIndication(simpleReport.getFirstSignatureId());

        listOfTrustedLists.setIndication(indication.name());
        if(subIndication!=null)
            listOfTrustedLists.setSubIndication(subIndication.name());
        else
            listOfTrustedLists.setSubIndication(null);

        listOfTrustedLists.setWarnings(new ArrayList<>());
        for(Message iter: warnings) {
            listOfTrustedLists.getWarnings().add(new STICustomMessage(iter.getKey(),iter.getValue()));
        }

        return listOfTrustedLists;
    }

    private STIListOfTrustedLists parseLotl(STIListOfTrustedLists listOfTrustedLists, String lotlXmlSource) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputStream inputStream = IOUtils.toInputStream(lotlXmlSource);
        Document doc = db.parse(inputStream);
        doc.getDocumentElement().normalize();
        inputStream.close();

        Element schemeInformation = (Element) doc.getElementsByTagName("SchemeInformation").item(0);
        Element schemeTerritory = (Element) doc.getElementsByTagName("SchemeTerritory").item(0);
        Element historicalInformationPeriod = (Element) doc.getElementsByTagName("HistoricalInformationPeriod").item(0);
        Element listIssueDateTime = (Element) doc.getElementsByTagName("ListIssueDateTime").item(0);
        Element nextUpdate = (Element) doc.getElementsByTagName("NextUpdate").item(0);
        nextUpdate = (Element) nextUpdate.getElementsByTagName("dateTime").item(0);
        Element distributionPoints = (Element) doc.getElementsByTagName("DistributionPoints").item(0);
        distributionPoints = (Element) distributionPoints.getElementsByTagName("URI").item(0);

        listOfTrustedLists.setSchemeTerritory(schemeTerritory.getTextContent());
        listOfTrustedLists.setHistoricalInformationPeriod(
                Integer.parseInt(historicalInformationPeriod.getTextContent())
        );
        listOfTrustedLists.setListIssueDateTime(listIssueDateTime.getTextContent());
        listOfTrustedLists.setNextUpdate(nextUpdate.getTextContent());
        listOfTrustedLists.setDistributionPoint(distributionPoints.getTextContent());

        Element pointersToOtherTSL = (Element) schemeInformation.getElementsByTagName("PointersToOtherTSL").item(0);

        listOfTrustedLists.setPointersToOtherTsl(STIUtil.parsePointersToOtherTsl(pointersToOtherTSL, false));
        listOfTrustedLists.setTslType(STITSLType.EUListOfTheLists);
        listOfTrustedLists.setLastUpdated(formatter.format(LocalDateTime.now()));

        Element tslVersionIdentifier = (Element) doc.getElementsByTagName("TSLVersionIdentifier").item(0);
        listOfTrustedLists.setTslVersionIdentifier(Integer.parseInt(tslVersionIdentifier.getTextContent()));

        Element tslSequenceNumber = (Element) doc.getElementsByTagName("TSLSequenceNumber").item(0);
        listOfTrustedLists.setTslSequenceNumber(Integer.parseInt(tslSequenceNumber.getTextContent()));

        Element schemeOperatorName = (Element) doc.getElementsByTagName("SchemeOperatorName").item(0);
        NodeList names = schemeOperatorName.getElementsByTagName("Name");
        for(int i=0; i< names.getLength(); i++) {
            if(names.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element name = (Element) names.item(i);
            if(name.getAttribute("xml:lang").equals("en")) {
                listOfTrustedLists.setSchemeOperatorName(name.getTextContent());
                break;
            }
        }

        Element schemeOperatorAddresses = (Element) doc.getElementsByTagName("SchemeOperatorAddress").item(0);
        Element schemeOperatorPostalAddresses = (Element) schemeOperatorAddresses.getElementsByTagName("PostalAddresses").item(0);
        Element SchemeOperatorElectronicAddresses = (Element) schemeOperatorAddresses.getElementsByTagName("ElectronicAddress").item(0);

        List<STIPostalAddress> postalAddresses = new ArrayList<>();
        List<String> emailAddresses = new ArrayList<>();

        NodeList tspPostalAddressesList = schemeOperatorPostalAddresses.getElementsByTagName("PostalAddress");
        for (int i = 0; i < tspPostalAddressesList.getLength(); i++) {
            if (tspPostalAddressesList.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element address = (Element) tspPostalAddressesList.item(i);
            if (!address.getAttribute("xml:lang").equalsIgnoreCase("en"))
                continue;
            STIPostalAddress postalAddress = new STIPostalAddress();
            postalAddress.setPostalCode(address.getElementsByTagName("StreetAddress").item(0).getTextContent());
            postalAddress.setLocality(address.getElementsByTagName("Locality").item(0).getTextContent());
            postalAddress.setCountryName(address.getElementsByTagName("CountryName").item(0).getTextContent());
            postalAddress.setStreetAddress(address.getElementsByTagName("StreetAddress").item(0).getTextContent());

            postalAddresses.add(postalAddress);
        }

        NodeList tspElectronicAddressesList = SchemeOperatorElectronicAddresses.getElementsByTagName("URI");
        for(int j=0; j< tspElectronicAddressesList.getLength(); j++) {
            if (tspElectronicAddressesList.item(j).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element uri = (Element) tspElectronicAddressesList.item(j);
            emailAddresses.add(uri.getTextContent());
        }

        listOfTrustedLists.setPostalAddresses(postalAddresses);
        listOfTrustedLists.setElectronicAddresses(emailAddresses);

        Element schemeName = (Element) doc.getElementsByTagName("SchemeName").item(0);
        names = schemeName.getElementsByTagName("Name");
        for(int i=0; i< names.getLength(); i++) {
            if(names.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element name = (Element) names.item(i);
            if(name.getAttribute("xml:lang").equals("en")) {
                listOfTrustedLists.setSchemeName(name.getTextContent());
                break;
            }
        }

        listOfTrustedLists.setSchemeInformationURI(new ArrayList<>());
        Element schemeInformationUri = (Element) doc.getElementsByTagName("SchemeInformationURI").item(0);
        NodeList uris = schemeInformationUri.getElementsByTagName("URI");
        for(int i=0; i< uris.getLength(); i++) {
            if(uris.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element uri = (Element) uris.item(i);
            if(uri.getAttribute("xml:lang").equals("en")) {
                listOfTrustedLists.getSchemeInformationURI().add(uri.getTextContent());
            }
        }

        Element statusDeterminationApproach = (Element) doc.getElementsByTagName("StatusDeterminationApproach").item(0);
        listOfTrustedLists.setStatusDeterminationApproach(statusDeterminationApproach.getTextContent());

        Element schemeTypeCommunityRules = (Element) doc.getElementsByTagName("SchemeTypeCommunityRules").item(0);
        uris = schemeTypeCommunityRules.getElementsByTagName("URI");
        for(int i=0; i< uris.getLength(); i++) {
            if(uris.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element uri = (Element) uris.item(i);
            if(uri.getAttribute("xml:lang").equals("en")) {
                listOfTrustedLists.setSchemeTypeCommunityRules(uri.getTextContent());
                break;
            }
        }

        Element policyOrLegalNotice = (Element) doc.getElementsByTagName("PolicyOrLegalNotice").item(0);
        NodeList tslLegalNotices = policyOrLegalNotice.getElementsByTagName("TSLLegalNotice");
        for(int i=0; i< tslLegalNotices.getLength(); i++) {
            if(tslLegalNotices.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element tslLegalNotice = (Element) tslLegalNotices.item(i);
            if(tslLegalNotice.getAttribute("xml:lang").equals("en")) {
                listOfTrustedLists.setPolicyOrLegalNotice(tslLegalNotice.getTextContent());
                break;
            }
        }

        return listOfTrustedLists;
    }

    public STIListOfTrustedLists getListOfTrustedLists() {
        try {
            STIListOfTrustedLists lotl = memoryCell.getLotl();

            while( lotl.getLastUpdated() == null ||
                    System.currentTimeMillis() -  Instant.from(formatter.parse(lotl.getLastUpdated())).toEpochMilli() > runInterval * 1000) {
                Thread.sleep(5000);
                lotl = memoryCell.getLotl();
            }
            return lotl;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Override
    public void run() {
        while (keepRunning()) {
            if (System.currentTimeMillis() - this.lastVerifyTimestamp > runInterval * 1000) {

                STIEuTLValidator instance = applicationContext.getBean("STIEuTLValidator", STIEuTLValidator.class);

                if (System.currentTimeMillis() - instance.getLastVerifyTimestamp() >
                        instance.getRunInterval() * 1000) {
                    log.warn("EuTLValidator.job.onlineRefresh() has not been run yet.");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                validationMutex.enter();
                log.info("Starting validation of LOTL");
                this.certificateVerifier = this.applicationContext.getBean("STIEuTLValidator", STIEuTLValidator.class).getCertificateVerifier();

                String lotlXmlSource = STIUtil.getResponseFromUrl(env.getProperty("dss.europa.tl.lotl_url"));
                STIListOfTrustedLists listOfTrustedLists =  this.verifyLotl(lotlXmlSource);
                this.parseLotl(listOfTrustedLists, lotlXmlSource);

                listOfTrustedLists.setLastUpdated(formatter.format(LocalDateTime.now()));
                memoryCell.setLotl(listOfTrustedLists);

                lastVerifyTimestamp = System.currentTimeMillis();
                log.info("Ended validation of LOTL");
                validationMutex.leave();
            }
        }
    }
}
