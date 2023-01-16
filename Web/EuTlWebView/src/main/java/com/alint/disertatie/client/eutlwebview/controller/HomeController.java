package com.alint.disertatie.client.eutlwebview.controller;

import com.alint.disertatie.client.eutlwebview.model.entity.ListOfTrustedLists;
import com.alint.disertatie.client.eutlwebview.model.entity.OtherTSLPointer;
import com.alint.disertatie.client.eutlwebview.model.util.ServerRetriever;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

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

        ListOfTrustedLists lotl = serverRetriever.getListOfTrustedLists();
        List<OtherTSLPointer> pointerList = lotl.getPointersToOtherTsl();
        model.addAttribute("EUPointer",lotl.getCCTSLPointer("EU"));
        model.addAttribute("UKPointer", lotl.getCCTSLPointer("UK"));
        pointerList.remove(lotl.getCCTSLPointer("UK"));
        pointerList.remove(lotl.getCCTSLPointer("EU"));
        model.addAttribute("pointerList",pointerList);

        return "tlbrowser-view";
    }


    public HomeController(ServerRetriever serverRetriever) {
        this.serverRetriever = serverRetriever;
    }
}
