package com.alint.disertatie.client.eutlwebview.controller;

import com.alint.disertatie.client.eutlwebview.enginecomm.STIJavaEngineTransceiver;
import com.alint.disertatie.client.eutlwebview.model.entity.STICertificateValidationResult;
import com.alint.disertatie.client.eutlwebview.model.entity.STISignatureValidationResult;
import com.alint.disertatie.client.eutlwebview.model.message.STICertificateValidationResponse;
import com.alint.disertatie.client.eutlwebview.model.message.STISignedFileValidationResponse;
import com.alint.disertatie.client.eutlwebview.util.STICertUtils;
import com.alint.disertatie.client.eutlwebview.util.STIUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;

@Controller
@RequestMapping("/validate")
@Log4j2
public class STIValidationController {

    private final String VALIDATE_CERTIFICATE_COMMAND = "validateCertificate";
    private final String VALIDATE_SIGNATURE_COMMAND = "validateSignature";
    private final Environment env;

    @Autowired
    public STIValidationController(Environment env) {
        this.env = env;
    }

    @PostMapping(value = "/certificate")
    public String validateCertificate(Model model, @RequestParam("valTime") String valTime, @Nullable @RequestParam("base64Certificate") String base64CertificateText, @Nullable @RequestParam("certificateFile") MultipartFile certificateFile, @RequestParam("encoding") String encoding) throws IOException {


        String base64Certificate = "UNINITIALIZED";
        if (encoding.equalsIgnoreCase("base64")) {
            base64Certificate = base64CertificateText;
        }
        if (encoding.equalsIgnoreCase("PEM")) {

            Scanner reader = new Scanner(new InputStreamReader(certificateFile.getInputStream()));
            StringBuilder contents = new StringBuilder();

            while (reader.hasNextLine()) {
                contents.append(reader.nextLine());
            }

            base64Certificate = contents.toString();
        }
        if (encoding.equalsIgnoreCase("DER")) {
            byte[] bytes = certificateFile.getBytes();

            base64Certificate = Base64.getEncoder().encodeToString(bytes);
        }
        if (base64Certificate.equals("UNINITIALIZED")) {
            throw new IOException("Unsupported certificate format");
        }
        String hostAddr;
        int hostPort;

        hostAddr = Objects.requireNonNull(env.getProperty("engine.java.addr"));
        hostPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("engine.java.port")));


        base64Certificate = STIUtil.cleanBase64Input(base64Certificate);

        try (Socket socket = new Socket(hostAddr, hostPort)) {
            ObjectMapper mapper = new ObjectMapper();
            STIJavaEngineTransceiver transceiver = new STIJavaEngineTransceiver(socket.getInputStream(), socket.getOutputStream());

            transceiver.sendMessage(VALIDATE_CERTIFICATE_COMMAND);


            transceiver.sendMessage(base64Certificate);

            if(valTime==null || valTime.equals("")){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
                valTime = sdf.format(new Date());
            }

            transceiver.sendMessage(valTime);

            String certValRespString = transceiver.readMessage();

            transceiver.closeConnections();
            STICertificateValidationResponse response = mapper.readValue(certValRespString, STICertificateValidationResponse.class);
            model.addAttribute("response", response);
            model.addAttribute("certInfo", STICertUtils.getCertificateInfo(base64Certificate));

            boolean validationStatus = true;
            boolean currentlyValidStatus = true;

            for (STICertificateValidationResult result : response.getCertificateChain()) {
                if (!result.getIndication().equalsIgnoreCase("PASSED")) {
                    validationStatus = false;
                    break;
                }
            }

            Date date = new Date(System.currentTimeMillis());

            currentlyValidStatus = validationStatus && date.compareTo(response.getCertificateChain().get(0).getNotBefore()) >= 0 && date.compareTo(response.getCertificateChain().get(0).getNotAfter()) <= 0;

            model.addAttribute("validationStatus", validationStatus);
            model.addAttribute("currentlyValidStatus", currentlyValidStatus);

            return "view - certificateVerificationResults";

        } catch (IOException exc) {
            exc.printStackTrace();
            throw new IOException(exc);
        }

    }

    @PostMapping("/signature")
    public String validateSignature(Model model, @RequestParam("signatureFile") MultipartFile signatureFile) throws IOException {

        byte[] bytes = signatureFile.getInputStream().readAllBytes();

        String hostAddr;
        int hostPort;

        hostAddr = Objects.requireNonNull(env.getProperty("engine.java.addr"));
        hostPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("engine.java.port")));

        try (Socket socket = new Socket(hostAddr, hostPort)) {
            STIJavaEngineTransceiver transceiver = new STIJavaEngineTransceiver(socket.getInputStream(), socket.getOutputStream());
            ObjectMapper mapper = new ObjectMapper();

            transceiver.sendMessage(VALIDATE_SIGNATURE_COMMAND);

            transceiver.sendBytes(bytes);

            String sigValRespString = transceiver.readMessage();

            transceiver.closeConnections();

            STISignedFileValidationResponse response = mapper.readValue(sigValRespString, STISignedFileValidationResponse.class);
            model.addAttribute("response", response.getResult());

            boolean indication = true;
            for (STISignatureValidationResult result : response.getResult().getSignatureResults()) {
                if (!result.getIndication().equalsIgnoreCase("TOTAL_PASSED")) {
                    indication = false;
                    break;
                }
            }
            model.addAttribute("indication", indication);
        }

        return "view - signatureVerificationResults";
    }
}
