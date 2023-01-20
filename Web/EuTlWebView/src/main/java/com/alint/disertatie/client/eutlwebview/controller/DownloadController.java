package com.alint.disertatie.client.eutlwebview.controller;

import com.alint.disertatie.client.eutlwebview.model.entity.ListOfTrustedLists;
import com.alint.disertatie.client.eutlwebview.model.entity.OtherTSLPointer;
import com.alint.disertatie.client.eutlwebview.util.ServerRetriever;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
@RequestMapping("/tldownloader")
public class DownloadController {

    private final ServerRetriever serverRetriever;

    @Autowired
    public DownloadController(ServerRetriever serverRetriever) {
        this.serverRetriever = serverRetriever;
    }

    @GetMapping("/{countryCode}")
    public HttpEntity<byte[]> getFile(@PathVariable("countryCode") String countryCode, HttpServletResponse response) {
        try{
            ListOfTrustedLists lotl = serverRetriever.getListOfTrustedLists();
            OtherTSLPointer pointer = lotl.getCCTSLPointer(countryCode);

            URL url = new URL(pointer.getTslLocation());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            byte[] documentBody = con.getInputStream().readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + countryCode + ".xml");
            return new HttpEntity<>(documentBody, headers);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}