package com.alint.disertatie.server.javaresteutlproviderapi.util;

import com.alint.disertatie.server.javaresteutlproviderapi.entity.*;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.*;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


@Log4j2
public class Util {

    private final static Map<String, TrustServiceType> UriToTypeMap;
    private final static Map<String, TrustServiceAdditionalType> UriToAdditionalTypeMap;
    private final static Map<String, TrustServiceStatus> UriToStatusMap;

    static {
        UriToTypeMap = new HashMap<>();
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/CA/QC",TrustServiceType.QC_CA);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP/QC",TrustServiceType.QC_OCSP);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/CRL/QC",TrustServiceType.QC_CRL);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/TSA/QTST",TrustServiceType.Q_TST);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/EDS/Q",TrustServiceType.Q_EDS);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/EDS/REM/Q",TrustServiceType.Q_REM);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/PSES/Q",TrustServiceType.Q_PSES);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/QESValidation/Q",TrustServiceType.Q_QESVAL);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/CA/PKC",TrustServiceType.NQC_CA);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP",TrustServiceType.NQC_OCSP);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/CRL",TrustServiceType.NQC_CRL);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/TSA",TrustServiceType.NQ_TST);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/TSA/TSS-QC",TrustServiceType.NQ_TSSQC);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/TSA/TSS-AdESQCandQES",TrustServiceType.NQ_TSSADESQCQES);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/EDS",TrustServiceType.NQ_EDS);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/EDS/REM",TrustServiceType.NQ_REMDS);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/PSES",TrustServiceType.NQ_PSES);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/AdESValidation",TrustServiceType.NQ_ADESV);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/AdESGeneration",TrustServiceType.NQ_ADESG);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/RA",TrustServiceType.NAT_RA);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/RA/nothavingPKIid",TrustServiceType.NAT_RANOPKI);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/ACA",TrustServiceType.NAT_ACA);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/SignaturePolicyAuthority",TrustServiceType.NAT_SPA);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/Archiv",TrustServiceType.NAT_ARCH);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/Archiv/nothavingPKIid",TrustServiceType.NAT_ARCHNOPKI);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/IdV",TrustServiceType.NAT_IDV);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/IdV/nothavingPKIid",TrustServiceType.NAT_IDVNOPKI);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/KEscrow",TrustServiceType.NAT_QESC);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/KEscrow/nothavingPKIid",TrustServiceType.NAT_QESCNOPKI);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/PPwd",TrustServiceType.NAT_PP);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/PPwd/nothavingPKIid",TrustServiceType.NAT_PPNOPKI);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvd/Svctype/TLIssuer",TrustServiceType.NAT_TLISS);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/NationalRootCA-QC",TrustServiceType.NAT_ROOTCA_QC);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/unspecified",TrustServiceType.UNSPECIFIED);

        UriToAdditionalTypeMap = new HashMap<>();
        UriToAdditionalTypeMap.put("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForeSignatures",TrustServiceAdditionalType.ForESignatures);
        UriToAdditionalTypeMap.put("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForeSeals",TrustServiceAdditionalType.ForESeals);
        UriToAdditionalTypeMap.put("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForWebSiteAuthentication",TrustServiceAdditionalType.ForWebSiteAuthentication);

        UriToStatusMap = new HashMap<>();
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/recognisedatnationallevel",TrustServiceStatus.NationalLevelRecognised);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/deprecatedatnationallevel",TrustServiceStatus.NationalLevelDeprecated);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/granted",TrustServiceStatus.Granted);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/withdrawn",TrustServiceStatus.Withdrawn);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/undersupervision",TrustServiceStatus.UnderSupervision);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/supervisionincessation",TrustServiceStatus.SupervisionInCessation);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/supervisionceased",TrustServiceStatus.SupervisionCeased);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/supervisionrevoked",TrustServiceStatus.SupervisionRevoked);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/accredited",TrustServiceStatus.Accredited);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/accreditationceased",TrustServiceStatus.AccreditationCeased);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/accreditationrevoked",TrustServiceStatus.AccreditationRevoked);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/setbynationallaw",TrustServiceStatus.SetByNationalLaw);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/deprecatedbynationallaw",TrustServiceStatus.DeprecatedByNationalLaw);
    }

    public static String getResponseFromUrl(String urlString) throws IOException {

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            response.append(line);
            response.append("\n");
        }
        response.deleteCharAt(response.lastIndexOf("\n"));
        reader.close();

        return response.toString();
    }

    public static List<OtherTSLPointer> parsePointersToOtherTsl(Element pointersToOtherTsl) {
        NodeList otherTslPointers = pointersToOtherTsl.getChildNodes();

        List<OtherTSLPointer> otslpointers = new ArrayList<>();

        for (int i = 0; i < otherTslPointers.getLength(); i++) {
            if (otherTslPointers.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;

            OtherTSLPointer otslpointer = new OtherTSLPointer();
            otslpointer.setTslType(TSLType.EUListOfTheLists);

            Element pointer = (Element) otherTslPointers.item(i);
            Element tslLocation = (Element) pointer.getElementsByTagName("TSLLocation").item(0);
            otslpointer.setTslLocation(tslLocation.getTextContent());

            Element aditionalInformation = (Element) pointer.getElementsByTagName("AdditionalInformation").item(0);

            NodeList otherInformationList = aditionalInformation.getChildNodes();

            for (int j = 0; j < otherInformationList.getLength(); j++) {

                if (otherInformationList.item(j).getNodeType() != Node.ELEMENT_NODE)
                    continue;

                Element otherInformation = (Element) otherInformationList.item(j);

                Element pointerSchemeTerritory = (Element) otherInformation.getElementsByTagName("SchemeTerritory").item(0);
                if (pointerSchemeTerritory != null) {
                    otslpointer.setSchemeTerritory(pointerSchemeTerritory.getTextContent());
                    continue;
                }

                Element mimeType = (Element) otherInformation.getElementsByTagName("ns3:MimeType").item(0);
                if (mimeType != null) {
                    String mimeTypeValue = mimeType.getTextContent();
                    if (mimeTypeValue.equals(MimeType.XML.getValue()))
                        otslpointer.setMimeType(MimeType.XML);
                    if (mimeTypeValue.equals(MimeType.PDF.getValue()))
                        otslpointer.setMimeType(MimeType.PDF);
                    continue;
                }

                Element schemeOperatorName = (Element) otherInformation.getElementsByTagName("SchemeOperatorName").item(0);
                if (schemeOperatorName != null) {
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
                otslpointers.add(otslpointer);
        }

        return otslpointers;
    }

    public static void parseTSPInformation(Element tspInformation, TrustServiceProvider tsp) {
        Element tspName = (Element) tspInformation.getElementsByTagName("TSPName").item(0);
        NodeList names = tspName.getElementsByTagName("Name");
        for (int i = 0; i < names.getLength(); i++) {
            Element name = (Element) names.item(i);
            if (name.getAttribute("xml:lang").equals("en")) {
                tsp.setName(name.getTextContent());
                break;
            }
        }

        Element tspTradeName = (Element) tspInformation.getElementsByTagName("TSPTradeName").item(0);
        if(tspTradeName == null) {
            log.warn("Found Trust Service Provider with null Trade Name.");
            tsp.setTradeName(null);
        }
        else {
            NodeList tradeNames = tspTradeName.getElementsByTagName("Name");
            for (int i = 0; i < names.getLength(); i++) {
                Element name = (Element) tradeNames.item(i);
                if (name.getAttribute("xml:lang").equals("en")) {
                    tsp.setTradeName(name.getTextContent());
                    break;
                }
            }
        }

        Element tspAddresses = (Element) tspInformation.getElementsByTagName("TSPAddress").item(0);
        Element tspPostalAddresses = (Element) tspAddresses.getElementsByTagName("PostalAddresses").item(0);
        Element tspElectronicAddresses = (Element) tspAddresses.getElementsByTagName("ElectronicAddress").item(0);

        List<PostalAddress> postalAddresses = new ArrayList<>();
        List<String> emailAddresses = new ArrayList<>();

        NodeList tspPostalAddressesList = tspPostalAddresses.getElementsByTagName("PostalAddress");
        for (int i = 0; i < tspPostalAddressesList.getLength(); i++) {
            if (tspPostalAddressesList.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element address = (Element) tspPostalAddressesList.item(i);
            if (!address.getAttribute("xml:lang").equalsIgnoreCase("en"))
                continue;
            PostalAddress postalAddress = new PostalAddress();
            postalAddress.setPostalCode(address.getElementsByTagName("StreetAddress").item(0).getTextContent());
            postalAddress.setLocality(address.getElementsByTagName("Locality").item(0).getTextContent());
            postalAddress.setCountryName(address.getElementsByTagName("CountryName").item(0).getTextContent());
            postalAddress.setStreetAddress(address.getElementsByTagName("StreetAddress").item(0).getTextContent());

            postalAddresses.add(postalAddress);
        }

        NodeList tspElectronicAddressesList = tspElectronicAddresses.getElementsByTagName("URI");
        for(int j=0; j< tspElectronicAddressesList.getLength(); j++) {
            if (tspElectronicAddressesList.item(j).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element uri = (Element) tspElectronicAddressesList.item(j);
            emailAddresses.add(uri.getTextContent());
            }

        tsp.setPostalAddresses(postalAddresses);
        tsp.setElectronicAddresses(emailAddresses);
    }

    public static List<TrustServiceHistoryInstance> parseTrustServiceHistory(Element serviceHistory) {
        if (serviceHistory == null)
            return null;

        List<TrustServiceHistoryInstance> history = new ArrayList<>();

        NodeList historyInstances = serviceHistory.getElementsByTagName("ServiceHistoryInstance");
        for(int i=0; i< historyInstances.getLength(); i++) {
            if(historyInstances.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element historyInstance = (Element) historyInstances.item(i);
            TrustServiceHistoryInstance trustServiceHistoryInstance = new TrustServiceHistoryInstance(
                    parseTrustService(historyInstance)
            );
            history.add(trustServiceHistoryInstance);
        }

        return history;
    }
    public static TrustService parseTrustService(Element service) {
        TrustService trustService = new TrustService();

        Element serviceTypeIdentifier = (Element) service.getElementsByTagName("ServiceTypeIdentifier").item(0);
        trustService.setServiceType(UriToTypeMap.get(serviceTypeIdentifier.getTextContent()));

        Element serviceStatus = (Element)service.getElementsByTagName("ServiceStatus").item(0);
        trustService.setServiceStatus(UriToStatusMap.get(serviceStatus.getTextContent()));

        Element statusStartingTime = (Element) service.getElementsByTagName("StatusStartingTime").item(0);
        trustService.setStatusStartingTime(statusStartingTime.getTextContent());

        Element serviceName = (Element) service.getElementsByTagName("ServiceName").item(0);
        NodeList serviceNames = serviceName.getElementsByTagName("Name");
        for (int i=0; i < serviceNames.getLength(); i++) {
            if(serviceNames.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element name = (Element) serviceNames.item(i);
            if(name.getAttribute("xml:lang").equalsIgnoreCase("en")) {
                trustService.setServiceName(name.getTextContent());
                break;
            }
        }

        List<DigitalId> serviceDigitalIds = new ArrayList<>();
        Element digitalIdentity = (Element) service.getElementsByTagName("ServiceDigitalIdentity").item(0);
        NodeList digitalIds = digitalIdentity.getElementsByTagName("DigitalId");
        for(int i=0; i< digitalIds.getLength(); i++) {
            if(digitalIds.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element digitalId = (Element) digitalIds.item(i);
            Element X509Certificate = (Element) digitalId.getElementsByTagName("X509Certificate").item(0);
            Element X509SubjectName = (Element) digitalId.getElementsByTagName("X509SubjectName").item(0);
            Element X509SKI         = (Element) digitalId.getElementsByTagName("X509SKI").item(0);

            if(X509Certificate!=null)
                serviceDigitalIds.add(new DigitalId(X509Certificate.getTextContent(),"X509Certificate"));
            if(X509SubjectName!=null)
                serviceDigitalIds.add(new DigitalId(X509SubjectName.getTextContent(), "X509SubjectName"));
            if(X509SKI!=null)
                serviceDigitalIds.add(new DigitalId(X509SKI.getTextContent(),"X509SKI"));
        }
        trustService.setDigitalIds(serviceDigitalIds);

        List<TrustServiceAdditionalType> additionalTypes = new ArrayList<>();

        Element serviceInformationExtensions = (Element) service.getElementsByTagName("ServiceInformationExtensions").item(0);
        if (serviceInformationExtensions!=null) {
            NodeList extensions = serviceInformationExtensions.getElementsByTagName("Extension");
            for (int i = 0; i < extensions.getLength(); i++) {
                if (extensions.item(i).getNodeType() != Node.ELEMENT_NODE)
                    continue;

                Element extension = (Element) extensions.item(i);
                extension = (Element) extension.getElementsByTagName("AdditionalServiceInformation").item(0);
                if (extension == null)
                    continue;
                extension = (Element) extension.getElementsByTagName("URI").item(0);

                additionalTypes.add(UriToAdditionalTypeMap.get(extension.getTextContent()));
            }
            trustService.setAdditionalTypes(additionalTypes);
        }


        return trustService;
    }

    public static List<TrustService> parseTrustServices(Element trustServices) {
        List<TrustService> trustServicesList = new ArrayList<>();
        NodeList trustServicesNodeList = trustServices.getElementsByTagName("TSPService");
        for (int i = 0; i < trustServicesNodeList.getLength(); i++) {
            if(trustServicesNodeList.item(i).getNodeType()!= Node.ELEMENT_NODE)
                continue;

            Element service = (Element) trustServicesNodeList.item(i);
            Element serviceInformation = (Element) service.getElementsByTagName("ServiceInformation").item(0);
            Element serviceHistory = (Element) service.getElementsByTagName("ServiceHistory").item(0);
            TrustService trustService = parseTrustService(serviceInformation);
            trustService.setServiceHistory(parseTrustServiceHistory(serviceHistory));

            trustServicesList.add(trustService);
        }

        return trustServicesList;
    }

    public static List<TrustServiceProvider> parseTrustServiceProviders(Element trustServiceProviderList) {
        List<TrustServiceProvider> trustServiceProvidersList = new ArrayList<>();
        NodeList trustServiceProviders = trustServiceProviderList.getElementsByTagName("TrustServiceProvider");

        for (int i = 0; i < trustServiceProviders.getLength(); i++) {
            if (trustServiceProviders.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element trustServiceProvider = (Element) trustServiceProviders.item(i);

            TrustServiceProvider tsp = new TrustServiceProvider();
            Element tspInformation = (Element) trustServiceProvider.getElementsByTagName("TSPInformation").item(0);
            Element tspServices = (Element) trustServiceProvider.getElementsByTagName("TSPServices").item(0);

            parseTSPInformation(tspInformation, tsp);
            tsp.setTrustServices(parseTrustServices(tspServices));

            trustServiceProvidersList.add(tsp);
        }

        return trustServiceProvidersList;
    }
}

