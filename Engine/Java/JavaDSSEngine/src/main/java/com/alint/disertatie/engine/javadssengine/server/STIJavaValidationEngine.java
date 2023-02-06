package com.alint.disertatie.engine.javadssengine.server;

import com.alint.disertatie.engine.javadssengine.dss.STIDSSValidator;
import com.alint.disertatie.engine.javadssengine.entity.*;
import com.alint.disertatie.engine.javadssengine.message.STICertificateValidationResponse;
import com.alint.disertatie.engine.javadssengine.message.STISignedFileValidationResponse;
import com.alint.disertatie.engine.javadssengine.util.STIJavaEngineTransceiver;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.simplecertificatereport.SimpleCertificateReport;
import eu.europa.esig.dss.simplecertificatereport.jaxb.XmlChainItem;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.simplereport.jaxb.XmlMessage;
import eu.europa.esig.dss.simplereport.jaxb.XmlSignature;
import eu.europa.esig.dss.simplereport.jaxb.XmlToken;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Log4j2
public class STIJavaValidationEngine {

    private final Environment env;
    private final ApplicationContext context;

    private final int serverPort;
    private final STIDSSValidator validator;

    @Autowired
    public STIJavaValidationEngine(Environment env, ApplicationContext context, STIDSSValidator validator) {
        this.env = env;
        this.context = context;

        this.serverPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("socket.port")));
        this.validator = validator;
    }

    public void execute() {
        ServerSocket server = null;

        try {
            server = new ServerSocket(serverPort);
            server.setReuseAddress(true);

            while (true) {
                Socket client = server.accept();

                log.info("New client connected: " + client.getInetAddress().getHostAddress());

                ClientHandler clientSock = new ClientHandler(client, validator);

                new Thread(clientSock).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static class ClientHandler implements Runnable {
        private final String VALIDATE_CERTIFICATE_COMMAND = "validateCertificate";
        private final String VALIDATE_SIGNATURE_COMMAND = "validateSignature";
        private final Socket clientSocket;
        private final STIDSSValidator validator;

        public ClientHandler(Socket clientSocket, STIDSSValidator validator) throws IOException {
            this.clientSocket = clientSocket;
            this.validator = validator;
        }

        @Override
        public void run() {


            try {
                STIJavaEngineTransceiver transceiver = new STIJavaEngineTransceiver(clientSocket.getInputStream(), clientSocket.getOutputStream());

                String command = transceiver.readMessage();
                switch (command) {
                    case VALIDATE_CERTIFICATE_COMMAND -> {
                        validateCertificate(transceiver);
                    }
                    case VALIDATE_SIGNATURE_COMMAND -> {
                        validateSignature(transceiver);

                    }
                    case "Test" -> {
                        transceiver.sendMessage(new StringBuilder(command).reverse().toString());
                    }
                    default -> {
                        transceiver.sendMessage("Unsupported Opperation");
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (!clientSocket.isClosed()) {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void validateCertificate(STIJavaEngineTransceiver transceiver) throws IOException, ParseException {
            String base64Certificate = transceiver.readMessage();
            String valTime = transceiver.readMessage();

            SimpleCertificateReport report = validator.validateCertificate(base64Certificate,valTime);
            STICertificateValidationResponse response = new STICertificateValidationResponse();

            response.setCertificateChain(new ArrayList<>());

            for (XmlChainItem certificate : report.getJaxbModel().getChain()) {
                STICertificateValidationResult result = new STICertificateValidationResult();

                result.setSubject(certificate.getSubject());

                result.setIndication(certificate.getIndication().name());
                SubIndication subIndication = certificate.getSubIndication();
                result.setSubIndication(subIndication == null ? null : subIndication.name());
                result.setNotAfter(certificate.getNotAfter());
                result.setNotBefore(certificate.getNotBefore());
                result.setValidationTime(report.getValidationTime());
                result.setKeyUsages(new ArrayList<>());

                result.setQualificationAtValidation(new STICertificateQualification());
                result.getQualificationAtValidation().setType(certificate.getQualificationAtValidation() == null ? null : certificate.getQualificationAtValidation().getType().name());
                result.getQualificationAtValidation().setLabel(certificate.getQualificationAtValidation() == null ? null : certificate.getQualificationAtValidation().getLabel());
                result.getQualificationAtValidation().setQscdStatus(certificate.getQualificationAtValidation() != null && certificate.getQualificationAtValidation().isQscd());
                result.getQualificationAtValidation().setQualifiedStatus(certificate.getQualificationAtValidation() != null && certificate.getQualificationAtValidation().isQc());

                result.setQualificationAtIssuance(new STICertificateQualification());
                result.getQualificationAtIssuance().setType(certificate.getQualificationAtIssuance() == null ? null : certificate.getQualificationAtIssuance().getType().name());
                result.getQualificationAtIssuance().setLabel(certificate.getQualificationAtIssuance() == null ? null : certificate.getQualificationAtIssuance().getLabel());
                result.getQualificationAtIssuance().setQscdStatus(certificate.getQualificationAtIssuance() != null && certificate.getQualificationAtIssuance().isQscd());
                result.getQualificationAtIssuance().setQualifiedStatus(certificate.getQualificationAtIssuance() != null && certificate.getQualificationAtIssuance().isQc());

                result.setRevocationStatus(new STIRevocationStatus());
                result.getRevocationStatus().setThisUpdate(certificate.getRevocation().getThisUpdate());
                result.getRevocationStatus().setRevocationDate(certificate.getRevocation().getRevocationDate());
                result.getRevocationStatus().setRevocationReason(certificate.getRevocation().getRevocationReason() == null ? null : certificate.getRevocation().getRevocationReason().name());

                certificate.getKeyUsages().forEach(keyUsageBit -> result.getKeyUsages().add(keyUsageBit.name()));

                response.getCertificateChain().add(result);
            }

            ObjectMapper mapper = new ObjectMapper();
            transceiver.sendMessage(mapper.writeValueAsString(response));
        }

        private void validateSignature(STIJavaEngineTransceiver transceiver) throws IOException {
            byte[] signatureFile = transceiver.readBytes();

            SimpleReport report = validator.validateSignature(signatureFile);

            STISignedFileValidationResponse response = new STISignedFileValidationResponse();

            STISignedFileValidationResult result = new STISignedFileValidationResult();

            result.setValidationPolicy(new STIValidationPolicy(report.getJaxbModel().getValidationPolicy().getPolicyName(),report.getJaxbModel().getValidationPolicy().getPolicyDescription()));

            result.setValidSignaturesCount(report.getValidSignaturesCount());
            result.setSignaturesCount(report.getSignaturesCount());
            result.setValidationTime(report.getValidationTime());

            result.setSignatureResults(new ArrayList<>());

            for(XmlToken signature : report.getJaxbModel().getSignatureOrTimestamp()) {
                XmlSignature xmlSignature = (XmlSignature) signature;
                STISignatureValidationResult signatureResult = new STISignatureValidationResult();
                signatureResult.setSigningTime(xmlSignature.getSigningTime());
                signatureResult.setBestSignatureTime(xmlSignature.getBestSignatureTime());
                signatureResult.setSignedBy(xmlSignature.getSignedBy());
                signatureResult.setSignatureLevel(xmlSignature.getSignatureLevel().getDescription());
                signatureResult.setSignatureFormat(xmlSignature.getSignatureFormat().name());
                signatureResult.setExtensionPeriodMin(xmlSignature.getExtensionPeriodMin());
                signatureResult.setExtensionPeriodMax(xmlSignature.getExtensionPeriodMax());
                signatureResult.setIndication(xmlSignature.getIndication().name());
                signatureResult.setSubIndication(xmlSignature.getSubIndication() == null ? null : xmlSignature.getSubIndication().name());

                signatureResult.setWarnings(new ArrayList<>());
                List<XmlMessage> warnings = new ArrayList<>();
                if (xmlSignature.getQualificationDetails() != null) {
                    warnings.addAll(xmlSignature.getQualificationDetails().getWarning());
                    warnings.addAll(xmlSignature.getQualificationDetails().getError());
                    warnings.addAll(xmlSignature.getQualificationDetails().getInfo());
                }
                if(xmlSignature.getAdESValidationDetails() != null) {
                    warnings.addAll(xmlSignature.getAdESValidationDetails().getWarning());
                    warnings.addAll(xmlSignature.getAdESValidationDetails().getError());
                    warnings.addAll(xmlSignature.getAdESValidationDetails().getInfo());
                }
                warnings.forEach(w -> signatureResult.getWarnings().add(new STICustomMessage(w.getKey(), w.getValue())));

                signatureResult.setSignatureScopes(new ArrayList<>());
                xmlSignature.getSignatureScope().forEach(s -> signatureResult.getSignatureScopes().add(s.getValue()));

                signatureResult.setCertificateChain(new ArrayList<>());
                xmlSignature.getCertificateChain().getCertificate().forEach(c -> signatureResult.getCertificateChain().add(c.getQualifiedName()));

                result.getSignatureResults().add(signatureResult);
            }

            response.setResult(result);

            ObjectMapper mapper = new ObjectMapper();
            transceiver.sendMessage(mapper.writeValueAsString(response));
        }
    }
}
