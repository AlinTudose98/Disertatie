package com.alint.disertatie.client.eutlwebview.controller;

import com.alint.disertatie.client.eutlwebview.model.util.ServerRetriever;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/")
public class HomeController {
    private final ServerRetriever serverRetriever;


    @GetMapping("/home")
    public String home() {
        return "home-view";
    }

    @GetMapping("/tlbrowser")
    public String tlbrowser(Model model) throws IOException {

        model.addAttribute("lotl",serverRetriever.getListOfTrustedLists());
        return "tlbrowser-view";
    }


    public HomeController(ServerRetriever serverRetriever) {
        this.serverRetriever = serverRetriever;
    }
}
