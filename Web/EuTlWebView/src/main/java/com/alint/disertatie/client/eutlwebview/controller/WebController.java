package com.alint.disertatie.client.eutlwebview.controller;

import com.alint.disertatie.client.eutlwebview.model.entity.*;
import com.alint.disertatie.client.eutlwebview.model.entry.TSPEntry;
import com.alint.disertatie.client.eutlwebview.model.enums.TrustServiceStatus;
import com.alint.disertatie.client.eutlwebview.util.ServerRetriever;
import com.alint.disertatie.client.eutlwebview.util.Util;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/")
@Log4j2
public class WebController {
    private final ServerRetriever serverRetriever;


    public WebController(ServerRetriever serverRetriever) {
        this.serverRetriever = serverRetriever;
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request) {
        log.trace("Got request /home from " + request.getRemoteAddr());

        return "home-view";
    }

    @GetMapping("/tlbrowser")
    public String tlbrowser(Model model, HttpServletRequest request) throws IOException {

        log.trace("Got request /tlbrowser from " + request.getRemoteAddr());

        ListOfTrustedLists lotl = serverRetriever.getListOfTrustedLists();
        List<OtherTSLPointer> pointerList = lotl.getPointersToOtherTsl();

        if (!lotl.getIndication().equalsIgnoreCase("TOTAL_PASSED")) {
            log.warn("List of trusted lists validation result: \t Indication: " + lotl.getIndication() + "\t Subindication: " + lotl.getSubIndication());
            model.addAttribute("indication", lotl.getIndication());
            model.addAttribute("subIndication", lotl.getSubIndication());
        }

        model.addAttribute("EUPointer", lotl.getCCTSLPointer("EU"));
        model.addAttribute("UKPointer", lotl.getCCTSLPointer("UK"));
        pointerList.remove(lotl.getCCTSLPointer("UK"));
        pointerList.remove(lotl.getCCTSLPointer("EU"));
        model.addAttribute("pointerList", pointerList);
        model.addAttribute("warnings", lotl.getWarnings());
        return "tlbrowser-view";
    }

    @GetMapping("/tlbrowser/{countryCode}")
    public String trustedListView(@PathVariable("countryCode") String countryCode, Model model, HttpServletRequest request) throws IOException {
        log.trace("Got request " + request.getRequestURI() + " from " + request.getRemoteAddr());

        if (!countryCode.equalsIgnoreCase("EU")) {
            TrustedList tl = serverRetriever.getTrustedList(countryCode);
            if (!tl.getIndication().equalsIgnoreCase("TOTAL_PASSED")) {
                log.warn("List of trusted lists validation result: \t Indication: " + tl.getIndication() + "\t Subindication: " + tl.getSubIndication());
                model.addAttribute("indication", tl.getIndication());
                model.addAttribute("subIndication", tl.getSubIndication());
            }

            model.addAttribute("schemeTerritory",tl.getSchemeTerritory());
            model.addAttribute("warnings", tl.getWarnings());
            List<TSPEntry> tsps = new ArrayList<>();
            List<TrustServiceProvider> tspList = tl.getTrustServiceProviders();
            Collections.sort(tspList, new Comparator<TrustServiceProvider>() {
                @Override
                public int compare(TrustServiceProvider o1, TrustServiceProvider o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            for (int i=0;i< tspList.size(); i++) {
                TSPEntry entry = new TSPEntry();
                entry.setId(i);
                entry.setTspName(tspList.get(i).getName());
                entry.setActiveStatus(false);
                for (TrustService ts : tspList.get(i).getTrustServices()) {
                    if (ts.getServiceStatus() == TrustServiceStatus.Granted || ts.getServiceStatus() == TrustServiceStatus.NationalLevelRecognised) {
                        entry.setActiveStatus(true);
                        entry.setQuals(Util.getServiceProviderQuals(tspList.get(i)));
                        Collections.sort(entry.getQuals());
                        break;
                    }

                }
                tsps.add(entry);
            }

            model.addAttribute("tspList", tsps);

            return "trustedlist-view";
        }
        model.addAttribute("lotl", serverRetriever.getListOfTrustedLists());
        return "listoftrustedlists-view";
    }
}
