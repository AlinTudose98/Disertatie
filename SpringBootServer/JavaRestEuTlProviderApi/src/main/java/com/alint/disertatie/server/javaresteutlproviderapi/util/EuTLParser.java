package com.alint.disertatie.server.javaresteutlproviderapi.util;


import com.alint.disertatie.server.javaresteutlproviderapi.entity.ListOfTrustedLists;
import com.alint.disertatie.server.javaresteutlproviderapi.entity.OtherTSLPointer;
import com.alint.disertatie.server.javaresteutlproviderapi.entity.TrustedList;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.MimeType;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.TSLType;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
@Log4j2
public class EuTLParser implements Runnable {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private final Monitor parsingMutex;
    private final Monitor validationMutex;
    private final Environment env;
    private boolean doStop = false;
    private ApplicationContext applicationContext;
    private ListOfTrustedLists listOfTrustedLists;
    private List<TrustedList> trustedLists;
    private long runInterval;
    private long lastVerifyTimestamp;
    private CertificateVerifier certificateVerifier;

    @Autowired
    public EuTLParser(ApplicationContext applicationContext, Environment env, @Qualifier("tlParserMutex") Monitor parsingMutex,
                      @Qualifier("tlValidationMutex") Monitor validationMutex) {
        this.applicationContext = applicationContext;
        this.env = env;
        this.parsingMutex = parsingMutex;
        this.validationMutex = validationMutex;

        this.lastVerifyTimestamp = 0;

        this.listOfTrustedLists = new ListOfTrustedLists();
        this.trustedLists = new ArrayList<>();
    }

    @PostConstruct
    private void configure() {
        this.runInterval = Long.parseLong(
                env.getProperty("dss.europa.tl.parser.run_interval_s", "600")
        );
        this.certificateVerifier = this.getApplicationContext()
                .getBean("euTLValidator", EuTLValidator.class).getCertificateVerifier();

        listOfTrustedLists.setTslType(TSLType.EUListOfTheLists);
    }


    public synchronized void doStop() {
        this.doStop = true;
    }

    public synchronized boolean keepRunning() {
        return !this.doStop;
    }

    private String verifyLotl() throws IOException {
        String lotlXmlSource = Util.getResponseFromUrl(
                env.getProperty("dss.europa.tl.lotl_url")
        );

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

        listOfTrustedLists.setWarnings(warnings);

        return lotlXmlSource;
    }

    private void parseLotl(String lotlXmlSource) throws IOException, SAXException, ParserConfigurationException {
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

        NodeList otherTslPointers = schemeInformation.getElementsByTagName("PointersToOtherTSL").item(0).getChildNodes();
        List<OtherTSLPointer> otslp = new ArrayList<>();

        for (int i = 0; i < otherTslPointers.getLength(); i++)
        {
            if(otherTslPointers.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;

            OtherTSLPointer otslpointer = new OtherTSLPointer();
            otslpointer.setTslType(TSLType.EUGeneric);

            Element pointer = (Element) otherTslPointers.item(i);
            Element tslLocation = (Element) pointer.getElementsByTagName("TSLLocation").item(0);
            otslpointer.setTslLocation(tslLocation.getTextContent());

            Element aditionalInformation = (Element) pointer.getElementsByTagName("AdditionalInformation").item(0);

            NodeList otherInformationList = aditionalInformation.getChildNodes();

            for(int j = 0; j < otherInformationList.getLength(); j ++) {

                if(otherInformationList.item(j).getNodeType() != Node.ELEMENT_NODE)
                    continue;

                Element otherInformation = (Element) otherInformationList.item(j);

                Element pointerSchemeTerritory = (Element) otherInformation.getElementsByTagName("SchemeTerritory").item(0);
                if(pointerSchemeTerritory != null) {
                    otslpointer.setSchemeTerritory(pointerSchemeTerritory.getTextContent());
                    continue;
                }

                Element mimeType = (Element) otherInformation.getElementsByTagName("ns3:MimeType").item(0);
                if(mimeType != null) {
                    String mimeTypeValue = mimeType.getTextContent();
                    if(mimeTypeValue.equals(MimeType.XML.getValue()))
                        otslpointer.setMimeType(MimeType.XML);
                    if(mimeTypeValue.equals(MimeType.PDF.getValue()))
                        otslpointer.setMimeType(MimeType.PDF);
                    continue;
                }

                Element schemeOperatorName = (Element) otherInformation.getElementsByTagName("SchemeOperatorName").item(0);
                if (schemeOperatorName!=null) {
                    NodeList names = schemeOperatorName.getElementsByTagName("Name");
                    for (int k = 0; k < names.getLength(); k++) {
                        Element name = (Element) names.item(k);
                        if (name.getAttribute("xml:lang").equals("en")) {
                            otslpointer.setSchemeOperatorName(name.getTextContent());
                        }
                    }
                }
            }

            if (otslpointer.getMimeType() == MimeType.XML)
                otslp.add(otslpointer);
        }
        listOfTrustedLists.setPointersToOtherTsl(otslp);
        listOfTrustedLists.setLastUpdated(formatter.format(LocalDateTime.now()));

    }

    @SneakyThrows
    @Override
    public void run() {
        while (keepRunning()) {
            if (System.currentTimeMillis() - this.lastVerifyTimestamp > runInterval * 1000) {

                parsingMutex.enter();

                EuTLValidator instance = applicationContext.getBean("euTLValidator", EuTLValidator.class);

                if (System.currentTimeMillis() - instance.getLastVerifyTimestamp() >
                        instance.getRunInterval() * 1000) {
                    log.warn("EuTLValidator.job.onlineRefresh() has not been run yet.");
                    parsingMutex.leave();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                validationMutex.enter();
                log.info("Starting validation of LOTL");
                this.certificateVerifier = this.applicationContext.getBean("euTLValidator", EuTLValidator.class).getCertificateVerifier();

                String lotlXmlSource = this.verifyLotl();
                this.parseLotl(lotlXmlSource);

                lastVerifyTimestamp = System.currentTimeMillis();
                log.info("Ended validation of LOTL");
                validationMutex.leave();
                parsingMutex.leave();
            }
        }
    }
}
