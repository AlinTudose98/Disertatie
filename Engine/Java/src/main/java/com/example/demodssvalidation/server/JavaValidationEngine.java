package com.example.demodssvalidation.server;

import com.example.demodssvalidation.dss.DSSValidator;
import com.example.demodssvalidation.message.CertificateValidationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.esig.dss.enumerations.KeyUsageBit;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.simplecertificatereport.SimpleCertificateReport;
import eu.europa.esig.dss.simplereport.SimpleReport;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Log4j2
public class JavaValidationEngine {

    private final Environment env;
    private final ApplicationContext context;

    private final int serverPort;
    private final DSSValidator validator;

    @Autowired
    public JavaValidationEngine(Environment env, ApplicationContext context,DSSValidator validator) {
        this.env = env;
        this.context = context;

        this.serverPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("socket.port")));
        this.validator = validator;
    }

    public void execute(){
        ServerSocket server = null;

        try {
            server = new ServerSocket(serverPort);
            server.setReuseAddress(true);

            while(true) {
                Socket client = server.accept();

                log.info("New client connected: " + client.getInetAddress().getHostAddress());

                ClientHandler clientSock = new ClientHandler(client, validator);

                new Thread(clientSock).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static class ClientHandler implements Runnable {
        private final String VALIDATE_CERTIFICATE_COMMAND = "validateCertificate";
        private final String VALIDATE_SIGNATURE_COMMAND = "validateSignature";
        private final Socket clientSocket;
        private final DSSValidator validator;

        DataOutputStream out;
        DataInputStream in;

        public ClientHandler(Socket clientSocket, DSSValidator validator) throws IOException {
            this.clientSocket = clientSocket;
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
            this.validator = validator;
        }

        @Override
        public void run() {


            try{
                String command = readMessage();
                switch (command) {
                    case VALIDATE_CERTIFICATE_COMMAND -> {
                        String base64Certificate = readMessage();
                        SimpleCertificateReport report = validator.validateCertificate(base64Certificate);
                        CertificateValidationResponse response = new CertificateValidationResponse();
                        response.setIndication(report.getCertificateIndication(report.getCertificateIds().get(0)).name());
                        SubIndication subIndication = report.getCertificateSubIndication(report.getCertificateIds().get(0));
                        response.setSubIndication(subIndication == null ? null : subIndication.name());
                        response.setNotAfter(report.getCertificateNotAfter(report.getCertificateIds().get(0)));
                        response.setNotBefore(report.getCertificateNotBefore(report.getCertificateIds().get(0)));
                        response.setValidationTime(report.getValidationTime());
                        List<KeyUsageBit> usageBits = report.getJaxbModel().getChain().get(0).getKeyUsages();
                        response.setKeyUsages(new ArrayList<>());
                        for(KeyUsageBit usage : usageBits) {
                            response.getKeyUsages().add(usage.name());
                        }

                        ObjectMapper mapper = new ObjectMapper();
                        sendMessage(mapper.writeValueAsString(response));
                    }
                    case VALIDATE_SIGNATURE_COMMAND -> {
                        byte[] document = readBytes();
                        SimpleReport report = validator.validateSignature(document);

                        sendMessage("Success");


                    }
                    case "Test" -> {
                        sendMessage(new StringBuilder(command).reverse().toString());
                    }
                    default -> {
                        sendMessage("Unsupported Opperation");
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if(out != null) {
                        out.close();
                    }
                    if(in != null) {
                        in.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        private void send(byte[] cbuf) throws IOException {
            out.write(cbuf);
            out.flush();
        }

        private void read(byte[] cbuf) throws IOException {
            int bytesRead = 0;
            int leftToRead = cbuf.length;
            while (leftToRead > 0) {
//                throw new IOException("Reading error: expected " + cbuf.length + " bytes but read " + bytesRead);
                int currBytesRead = in.read(cbuf,bytesRead, leftToRead);
                bytesRead += currBytesRead;
                leftToRead -= currBytesRead;
            }
        }

        public void sendUInt32(int toSend) throws IOException {
            if (Integer.compareUnsigned(toSend, Integer.MAX_VALUE) > 0) {
                throw new RuntimeException("Could not send an integer bigger than 4 bytes");
            }
            byte[] cbuf = new byte[4];
            cbuf[0] = (byte) (toSend & 0x000000FF);
            cbuf[1] = (byte) ((toSend & 0x0000FF00) >> 8);
            cbuf[2] = (byte) ((toSend & 0x00FF0000) >> 16);
            cbuf[3] = (byte) ((toSend & 0xFF000000) >> 24);

            send(cbuf);
        }

        public int readUInt32() throws IOException {
            byte[] cbuf = new byte[4];
            read(cbuf);

            return ByteBuffer.wrap(cbuf).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }

        public void sendMessage(String message) throws IOException {
            sendUInt32(message.length());
            send(message.getBytes(StandardCharsets.UTF_8));
        }

        public String readMessage() throws IOException {
            int messageLen = readUInt32();
            byte[] cbuf = new byte[messageLen];
            read(cbuf);

            return new String(cbuf,StandardCharsets.UTF_8);
        }
        public void sendBytes(byte[] bytes) throws IOException {
            sendUInt32(bytes.length);
            send(bytes);
        }

        public byte[] readBytes() throws IOException {
            int messageLen = readUInt32();
            byte[] cbuf = new byte[messageLen];
            read(cbuf);

            return cbuf;
        }
    }
}
