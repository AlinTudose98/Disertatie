package com.alint.disertatie.server.javaresteutlproviderapi.util;

import com.alint.disertatie.server.javaresteutlproviderapi.entity.*;
import com.alint.disertatie.server.javaresteutlproviderapi.enums.*;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


@Log4j2
public class STIUtil {

    private final static Map<String, STITrustServiceType> UriToTypeMap;
    private final static Map<String, STITrustServiceAdditionalType> UriToAdditionalTypeMap;
    private final static Map<String, STITrustServiceStatus> UriToStatusMap;

    static {
        UriToTypeMap = new HashMap<>();
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/CA/QC", STITrustServiceType.QC_CA);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP/QC", STITrustServiceType.QC_OCSP);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/CRL/QC", STITrustServiceType.QC_CRL);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/TSA/QTST", STITrustServiceType.Q_TST);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/EDS/Q", STITrustServiceType.Q_EDS);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/EDS/REM/Q", STITrustServiceType.Q_REM);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/PSES/Q", STITrustServiceType.Q_PSES);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/QESValidation/Q", STITrustServiceType.Q_QESVAL);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/CA/PKC", STITrustServiceType.NQC_CA);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP", STITrustServiceType.NQC_OCSP);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/CRL", STITrustServiceType.NQC_CRL);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/TSA", STITrustServiceType.NQ_TST);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/TSA/TSS-QC", STITrustServiceType.NQ_TSSQC);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/TSA/TSS-AdESQCandQES", STITrustServiceType.NQ_TSSADESQCQES);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/EDS", STITrustServiceType.NQ_EDS);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/EDS/REM", STITrustServiceType.NQ_REMDS);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/PSES", STITrustServiceType.NQ_PSES);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/AdESValidation", STITrustServiceType.NQ_ADESV);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/AdESGeneration", STITrustServiceType.NQ_ADESG);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/RA", STITrustServiceType.NAT_RA);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/RA/nothavingPKIid", STITrustServiceType.NAT_RANOPKI);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/ACA", STITrustServiceType.NAT_ACA);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/SignaturePolicyAuthority", STITrustServiceType.NAT_SPA);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/Archiv", STITrustServiceType.NAT_ARCH);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/Archiv/nothavingPKIid", STITrustServiceType.NAT_ARCHNOPKI);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/IdV", STITrustServiceType.NAT_IDV);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/IdV/nothavingPKIid", STITrustServiceType.NAT_IDVNOPKI);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/KEscrow", STITrustServiceType.NAT_QESC);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/KEscrow/nothavingPKIid", STITrustServiceType.NAT_QESCNOPKI);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/PPwd", STITrustServiceType.NAT_PP);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/PPwd/nothavingPKIid", STITrustServiceType.NAT_PPNOPKI);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvd/Svctype/TLIssuer", STITrustServiceType.NAT_TLISS);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/NationalRootCA-QC", STITrustServiceType.NAT_ROOTCA_QC);
        UriToTypeMap.put("http://uri.etsi.org/TrstSvc/Svctype/unspecified", STITrustServiceType.UNSPECIFIED);

        UriToAdditionalTypeMap = new HashMap<>();
        UriToAdditionalTypeMap.put("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForeSignatures", STITrustServiceAdditionalType.ForESignatures);
        UriToAdditionalTypeMap.put("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForeSeals", STITrustServiceAdditionalType.ForESeals);
        UriToAdditionalTypeMap.put("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForWebSiteAuthentication", STITrustServiceAdditionalType.ForWebSiteAuthentication);

        UriToStatusMap = new HashMap<>();
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/recognisedatnationallevel", STITrustServiceStatus.NationalLevelRecognised);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/deprecatedatnationallevel", STITrustServiceStatus.NationalLevelDeprecated);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/granted", STITrustServiceStatus.Granted);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/withdrawn", STITrustServiceStatus.Withdrawn);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/undersupervision", STITrustServiceStatus.UnderSupervision);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/supervisionincessation", STITrustServiceStatus.SupervisionInCessation);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/supervisionceased", STITrustServiceStatus.SupervisionCeased);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/supervisionrevoked", STITrustServiceStatus.SupervisionRevoked);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/accredited", STITrustServiceStatus.Accredited);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/accreditationceased", STITrustServiceStatus.AccreditationCeased);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/accreditationrevoked", STITrustServiceStatus.AccreditationRevoked);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/setbynationallaw", STITrustServiceStatus.SetByNationalLaw);
        UriToStatusMap.put("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/deprecatedbynationallaw", STITrustServiceStatus.DeprecatedByNationalLaw);
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

    public static List<STIOtherTSLPointer> parsePointersToOtherTsl(Element pointersToOtherTsl, boolean nsMode) {
        String ns ="";
        if(nsMode)
            ns="tsl:";

        NodeList otherTslPointers = pointersToOtherTsl.getChildNodes();

        List<STIOtherTSLPointer> otslpointers = new ArrayList<>();

        for (int i = 0; i < otherTslPointers.getLength(); i++) {
            if (otherTslPointers.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;

            STIOtherTSLPointer otslpointer = new STIOtherTSLPointer();
            //otslpointer.setTslType(TSLType.EUListOfTheLists);

            Element pointer = (Element) otherTslPointers.item(i);
            Element tslLocation = (Element) pointer.getElementsByTagName(ns + "TSLLocation").item(0);
            otslpointer.setTslLocation(tslLocation.getTextContent());

            Element aditionalInformation = (Element) pointer.getElementsByTagName(ns + "AdditionalInformation").item(0);

            NodeList otherInformationList = aditionalInformation.getChildNodes();

            for (int j = 0; j < otherInformationList.getLength(); j++) {

                if (otherInformationList.item(j).getNodeType() != Node.ELEMENT_NODE)
                    continue;

                Element otherInformation = (Element) otherInformationList.item(j);

                Element tslType = (Element) otherInformation.getElementsByTagName(ns + "TSLType").item(0);
                if(tslType != null){
                    String tslTypeValue = tslType.getTextContent();
                    if(tslTypeValue.equals(STITSLType.EUListOfTheLists.getValue()))
                        otslpointer.setTslType(STITSLType.EUListOfTheLists);
                    if(tslTypeValue.equals(STITSLType.EUGeneric.getValue()))
                        otslpointer.setTslType(STITSLType.EUGeneric);
                    if(tslTypeValue.equals(STITSLType.GenericNonEu.getValue()))
                        otslpointer.setTslType(STITSLType.GenericNonEu);
                    if(tslTypeValue.equals(STITSLType.OTHER.getValue()))
                        otslpointer.setTslType(STITSLType.OTHER);
                }

                Element pointerSchemeTerritory = (Element) otherInformation.getElementsByTagName(ns + "SchemeTerritory").item(0);
                if (pointerSchemeTerritory != null) {
                    otslpointer.setSchemeTerritory(pointerSchemeTerritory.getTextContent());
                    continue;
                }

                Element mimeType = (Element) otherInformation.getElementsByTagName("ns3:MimeType").item(0);
                if (mimeType == null)
                    mimeType = (Element) otherInformation.getElementsByTagName("tslx:MimeType").item(0);
                if (mimeType == null)
                    mimeType = (Element) otherInformation.getElementsByTagName("ns5:MimeType").item(0);
                if (mimeType == null)
                    mimeType = (Element) otherInformation.getElementsByTagName("ns4:MimeType").item(0);

                if (mimeType != null) {
                    String mimeTypeValue = mimeType.getTextContent();
                    if (mimeTypeValue.equals(STIMimeType.XML.getValue()))
                        otslpointer.setMimeType(STIMimeType.XML);
                    if (mimeTypeValue.equals(STIMimeType.PDF.getValue()))
                        otslpointer.setMimeType(STIMimeType.PDF);
                    continue;
                }

                Element schemeOperatorName = (Element) otherInformation.getElementsByTagName(ns + "SchemeOperatorName").item(0);
                if (schemeOperatorName != null) {
                    NodeList names = schemeOperatorName.getElementsByTagName(ns + "Name");
                    for (int k = 0; k < names.getLength(); k++) {
                        Element name = (Element) names.item(k);
                        if (name.getAttribute("xml:lang").equals("en")) {
                            otslpointer.setSchemeOperatorName(name.getTextContent());
                        }
                    }
                }
            }
            if (otslpointer.getMimeType() == STIMimeType.XML)
                otslpointers.add(otslpointer);
        }

        return otslpointers;
    }

    public static void parseTSPInformation(Element tspInformation, STITrustServiceProvider tsp, boolean nsMode) {
        String ns = "";
        if(nsMode)
            ns="tsl:";

        Element tspName = (Element) tspInformation.getElementsByTagName(ns + "TSPName").item(0);
        NodeList names = tspName.getElementsByTagName(ns + "Name");
        for (int i = 0; i < names.getLength(); i++) {
            Element name = (Element) names.item(i);
            if (name.getAttribute("xml:lang").equals("en")) {
                tsp.setName(name.getTextContent());
                break;
            }
        }

        Element tspTradeName = (Element) tspInformation.getElementsByTagName(ns + "TSPTradeName").item(0);
        if(tspTradeName == null) {
            log.warn("Found Trust Service Provider with null Trade Name.");
            tsp.setTradeNames(null);
        }
        else {
            tsp.setTradeNames(new ArrayList<>());
            NodeList tradeNames = tspTradeName.getElementsByTagName(ns + "Name");
            for (int i = 0; i < tradeNames.getLength(); i++) {
                Element name = (Element) tradeNames.item(i);
                if (name.getAttribute("xml:lang").equals("en")) {
                    tsp.getTradeNames().add(name.getTextContent());
                }
            }
        }

        Element tspAddresses = (Element) tspInformation.getElementsByTagName(ns + "TSPAddress").item(0);
        Element tspPostalAddresses = (Element) tspAddresses.getElementsByTagName(ns + "PostalAddresses").item(0);
        Element tspElectronicAddresses = (Element) tspAddresses.getElementsByTagName(ns + "ElectronicAddress").item(0);

        List<STIPostalAddress> postalAddresses = new ArrayList<>();
        List<String> emailAddresses = new ArrayList<>();

        NodeList tspPostalAddressesList = tspPostalAddresses.getElementsByTagName(ns + "PostalAddress");
        for (int i = 0; i < tspPostalAddressesList.getLength(); i++) {
            if (tspPostalAddressesList.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element address = (Element) tspPostalAddressesList.item(i);
            if (!address.getAttribute("xml:lang").equalsIgnoreCase("en"))
                continue;
            STIPostalAddress postalAddress = new STIPostalAddress();
            postalAddress.setPostalCode(address.getElementsByTagName(ns + "StreetAddress").item(0).getTextContent());
            postalAddress.setLocality(address.getElementsByTagName(ns + "Locality").item(0).getTextContent());
            postalAddress.setCountryName(address.getElementsByTagName(ns + "CountryName").item(0).getTextContent());
            postalAddress.setStreetAddress(address.getElementsByTagName(ns + "StreetAddress").item(0).getTextContent());

            postalAddresses.add(postalAddress);
        }

        NodeList tspElectronicAddressesList = tspElectronicAddresses.getElementsByTagName(ns + "URI");
        for(int j=0; j< tspElectronicAddressesList.getLength(); j++) {
            if (tspElectronicAddressesList.item(j).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element uri = (Element) tspElectronicAddressesList.item(j);
            emailAddresses.add(uri.getTextContent());
            }

        tsp.setPostalAddresses(postalAddresses);
        tsp.setElectronicAddresses(emailAddresses);


        Element tspInformationUri = (Element) tspInformation.getElementsByTagName(ns + "TSPInformationURI").item(0);
        NodeList uris = tspInformationUri.getElementsByTagName("URI");
        for(int i=0;i< uris.getLength(); i++) {
            Element uri = (Element) uris.item(i);
            if(uri.getAttribute("xml:lang").equalsIgnoreCase("en")) {
                tsp.setInformationUri(uri.getTextContent());
            }
        }
    }

    public static List<STITrustServiceHistoryInstance> parseTrustServiceHistory(Element serviceHistory, boolean nsMode) {
        String ns = "";
        if(nsMode)
            ns="tsl:";

        if (serviceHistory == null)
            return null;

        List<STITrustServiceHistoryInstance> history = new ArrayList<>();

        NodeList historyInstances = serviceHistory.getElementsByTagName(ns + "ServiceHistoryInstance");
        for(int i=0; i< historyInstances.getLength(); i++) {
            if(historyInstances.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element historyInstance = (Element) historyInstances.item(i);
            STITrustServiceHistoryInstance trustServiceHistoryInstance = new STITrustServiceHistoryInstance(
                    parseTrustService(historyInstance, nsMode)
            );
            history.add(trustServiceHistoryInstance);
        }

        return history;
    }
    public static STITrustService parseTrustService(Element service, boolean nsMode) {
        String ns="";
        if(nsMode)
            ns="tsl:";

        STITrustService trustService = new STITrustService();

        Element serviceTypeIdentifier = (Element) service.getElementsByTagName(ns + "ServiceTypeIdentifier").item(0);
        trustService.setServiceType(UriToTypeMap.get(serviceTypeIdentifier.getTextContent()));

        Element serviceStatus = (Element)service.getElementsByTagName(ns + "ServiceStatus").item(0);
        trustService.setServiceStatus(UriToStatusMap.get(serviceStatus.getTextContent()));

        Element statusStartingTime = (Element) service.getElementsByTagName(ns + "StatusStartingTime").item(0);
        trustService.setStatusStartingTime(statusStartingTime.getTextContent());

        Element serviceName = (Element) service.getElementsByTagName(ns + "ServiceName").item(0);
        NodeList serviceNames = serviceName.getElementsByTagName(ns + "Name");
        for (int i=0; i < serviceNames.getLength(); i++) {
            if(serviceNames.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element name = (Element) serviceNames.item(i);
            if(name.getAttribute("xml:lang").equalsIgnoreCase("en")) {
                trustService.setServiceName(name.getTextContent());
                break;
            }
        }

        List<STIDigitalId> serviceDigitalIds = new ArrayList<>();
        Element digitalIdentity = (Element) service.getElementsByTagName(ns + "ServiceDigitalIdentity").item(0);
        NodeList digitalIds = digitalIdentity.getElementsByTagName(ns + "DigitalId");
        for(int i=0; i< digitalIds.getLength(); i++) {
            if(digitalIds.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element digitalId = (Element) digitalIds.item(i);
            Element X509Certificate = (Element) digitalId.getElementsByTagName(ns + "X509Certificate").item(0);
            Element X509SubjectName = (Element) digitalId.getElementsByTagName(ns + "X509SubjectName").item(0);
            Element X509SKI         = (Element) digitalId.getElementsByTagName(ns + "X509SKI").item(0);

            if(X509Certificate!=null)
                serviceDigitalIds.add(new STIDigitalId(X509Certificate.getTextContent(),"X509Certificate"));
            if(X509SubjectName!=null)
                serviceDigitalIds.add(new STIDigitalId(X509SubjectName.getTextContent(), "X509SubjectName"));
            if(X509SKI!=null)
                serviceDigitalIds.add(new STIDigitalId(X509SKI.getTextContent(),"X509SKI"));
        }
        trustService.setDigitalIds(serviceDigitalIds);

        List<STITrustServiceAdditionalType> additionalTypes = new ArrayList<>();

        Element serviceInformationExtensions = (Element) service.getElementsByTagName(ns + "ServiceInformationExtensions").item(0);
        if (serviceInformationExtensions!=null) {
            NodeList extensions = serviceInformationExtensions.getElementsByTagName(ns + "Extension");
            for (int i = 0; i < extensions.getLength(); i++) {
                if (extensions.item(i).getNodeType() != Node.ELEMENT_NODE)
                    continue;

                Element extension = (Element) extensions.item(i);
                extension = (Element) extension.getElementsByTagName(ns + "AdditionalServiceInformation").item(0);
                if (extension == null)
                    continue;
                extension = (Element) extension.getElementsByTagName(ns + "URI").item(0);

                if(UriToAdditionalTypeMap.get(extension.getTextContent()) != null)
                    additionalTypes.add(UriToAdditionalTypeMap.get(extension.getTextContent()));
            }
            trustService.setAdditionalTypes(additionalTypes);
        }


        return trustService;
    }

    public static List<STITrustService> parseTrustServices(Element trustServices, boolean nsMode) {
        String ns="";
        if(nsMode)
            ns="tsl:";

        List<STITrustService> trustServicesList = new ArrayList<>();
        NodeList trustServicesNodeList = trustServices.getElementsByTagName(ns + "TSPService");
        for (int i = 0; i < trustServicesNodeList.getLength(); i++) {
            if(trustServicesNodeList.item(i).getNodeType()!= Node.ELEMENT_NODE)
                continue;

            Element service = (Element) trustServicesNodeList.item(i);
            Element serviceInformation = (Element) service.getElementsByTagName(ns + "ServiceInformation").item(0);
            Element serviceHistory = (Element) service.getElementsByTagName(ns + "ServiceHistory").item(0);
            STITrustService trustService = parseTrustService(serviceInformation, nsMode);
            trustService.setServiceHistory(parseTrustServiceHistory(serviceHistory, nsMode));

            trustServicesList.add(trustService);
        }

        return trustServicesList;
    }

    public static List<STITrustServiceProvider> parseTrustServiceProviders(Element trustServiceProviderList, boolean nsMode) {
        String ns = "";
        if (nsMode)
            ns = "tsl:";

        List<STITrustServiceProvider> trustServiceProvidersList = new ArrayList<>();
        NodeList trustServiceProviders = trustServiceProviderList.getElementsByTagName(ns + "TrustServiceProvider");

        for (int i = 0; i < trustServiceProviders.getLength(); i++) {
            if (trustServiceProviders.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element trustServiceProvider = (Element) trustServiceProviders.item(i);

            STITrustServiceProvider tsp = new STITrustServiceProvider();
            Element tspInformation = (Element) trustServiceProvider.getElementsByTagName(ns + "TSPInformation").item(0);
            Element tspServices = (Element) trustServiceProvider.getElementsByTagName(ns + "TSPServices").item(0);

            parseTSPInformation(tspInformation, tsp, nsMode);
            tsp.setTrustServices(parseTrustServices(tspServices, nsMode));

            trustServiceProvidersList.add(tsp);
        }

        return trustServiceProvidersList;
    }
}

