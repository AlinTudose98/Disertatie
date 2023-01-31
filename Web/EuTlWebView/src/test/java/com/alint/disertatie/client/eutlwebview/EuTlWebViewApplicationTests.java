package com.alint.disertatie.client.eutlwebview;

import com.alint.disertatie.client.eutlwebview.model.entity.CertificateInfo;
import com.alint.disertatie.client.eutlwebview.model.entity.ListOfTrustedLists;
import com.alint.disertatie.client.eutlwebview.util.CertUtils;
import com.alint.disertatie.client.eutlwebview.enginecomm.JavaEngineTransceiver;
import com.alint.disertatie.client.eutlwebview.util.ServerRetriever;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class EuTlWebViewApplicationTests {

    ApplicationContext context;

    @Autowired
    public EuTlWebViewApplicationTests(ApplicationContext context) {
        this.context = context;
    }

    @Test
    void contextLoads() {
    }

    @Test
    void ServerRetrievesWell() throws IOException {
        ServerRetriever serverRetriever = context.getBean("serverRetriever", ServerRetriever.class);

        ListOfTrustedLists lotl = serverRetriever.getListOfTrustedLists();
        System.out.println(lotl);
    }

    @Test
    void checkCertificateInfo() throws IOException {
        String base64Certificate = "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";

        CertificateInfo info = CertUtils.getCertificateInfo(base64Certificate);

        System.out.println(info);

    }

    @Test
    void checkJavaEngineCommunication() {
        String message = "Test";

        try (Socket socket = new Socket("localhost", 30001)) {
            JavaEngineTransceiver transceiver = new JavaEngineTransceiver(socket.getInputStream(), socket.getOutputStream());

            System.out.println("Sending message from client: ");
            System.out.println(message);
            transceiver.sendMessage(message);

            String response = transceiver.readMessage();
            System.out.println("Received message from server: ");
            System.out.println(response);

            assertEquals(message,new StringBuilder(response).reverse().toString());
        }
        catch (IOException exc) {
            exc.printStackTrace();
            throw new RuntimeException(exc);
        }
    }

    @Test
    void checkJavaEngineDocumentSignatureValidation() throws IOException {
        try (Socket socket = new Socket("localhost", 30001)) {
            String command = "validateSignature";
            JavaEngineTransceiver transceiver = new JavaEngineTransceiver(socket.getInputStream(), socket.getOutputStream());

            transceiver.sendMessage(command);


            byte[] document = Files.readAllBytes(Path.of("C:/Users/Alin Tudose/Desktop/lotl.xml"));

            transceiver.sendBytes(document);

            String response = transceiver.readMessage();
            System.out.println(response);
        }
    }
}
