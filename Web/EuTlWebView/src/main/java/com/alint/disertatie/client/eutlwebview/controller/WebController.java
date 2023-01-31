package com.alint.disertatie.client.eutlwebview.controller;

import com.alint.disertatie.client.eutlwebview.model.entity.*;
import com.alint.disertatie.client.eutlwebview.model.entry.TSPEntry;
import com.alint.disertatie.client.eutlwebview.model.entry.TrustServiceEntry;
import com.alint.disertatie.client.eutlwebview.model.entry.TrustServiceHistoryEntry;
import com.alint.disertatie.client.eutlwebview.model.enums.TrustServiceStatus;
import com.alint.disertatie.client.eutlwebview.util.CertUtils;
import com.alint.disertatie.client.eutlwebview.util.Countries;
import com.alint.disertatie.client.eutlwebview.util.ServerRetriever;
import com.alint.disertatie.client.eutlwebview.util.Util;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/")
@Log4j2
public class WebController {
    private final ApplicationContext context;
    private final ServerRetriever serverRetriever;


    @Autowired
    public WebController(ServerRetriever serverRetriever, ApplicationContext context) {
        this.context = context;
        this.serverRetriever = serverRetriever;
    }


    @GetMapping("/home/")
    public String homeRoute(HttpServletRequest request) {
        return home(request);
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request) {
        log.trace("Got request /home from " + request.getRemoteAddr());

        return "view - home";
    }

    @GetMapping("/tlbrowser/")
    public String tlbrowserRoute(Model model, HttpServletRequest request) throws IOException {
        return tlbrowser(model, request);
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

        pointerList.sort(Comparator.comparing(o -> Countries.getCountry(o.getSchemeTerritory())));

        model.addAttribute("pointerList", pointerList);
        model.addAttribute("warnings", lotl.getWarnings());
        return "view - tlbrowser";
    }

    @GetMapping("/tlbrowser/{countryCode}/")
    public String trustedListViewRoute(@PathVariable("countryCode") String countryCode, Model model, HttpServletRequest request) throws IOException, ParseException {
        return trustedListView(countryCode, model, request);
    }

    @GetMapping("/tlbrowser/{countryCode}")
    public String trustedListView(@PathVariable("countryCode") String countryCode, Model model, HttpServletRequest request) throws IOException, ParseException {
        log.trace("Got request " + request.getRequestURI() + " from " + request.getRemoteAddr());

        if (!countryCode.equalsIgnoreCase("EU")) {
            TrustedList tl = serverRetriever.getTrustedList(countryCode);
            if (!tl.getIndication().equalsIgnoreCase("TOTAL_PASSED")) {
                log.warn("Trusted list " + countryCode + " validation result: \t Indication: " + tl.getIndication() + "\t Subindication: " + tl.getSubIndication());
                model.addAttribute("indication", tl.getIndication());
                model.addAttribute("subIndication", tl.getSubIndication());
            }

            model.addAttribute("schemeTerritory", tl.getSchemeTerritory());
            model.addAttribute("warnings", tl.getWarnings());
            List<TSPEntry> tsps = new ArrayList<>();
            List<TrustServiceProvider> tspList = tl.getTrustServiceProviders();

            for (int i = 0; i < tspList.size(); i++) {
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

            tsps.sort(Comparator.comparing(TSPEntry::getTspName));

            for (TSPEntry tsp : tsps) {
                if (!tsp.isActiveStatus()) model.addAttribute("displayInactive", true);
            }

            model.addAttribute("tspList", tsps);
            model.addAttribute("otslp", tl.getPointersToOtherTSL());
            model.addAttribute("tl", tl);

            SimpleDateFormat parser = context.getBean("getParserDateTimeFormat", SimpleDateFormat.class);
            SimpleDateFormat printer = context.getBean("getPrinterDateTimeFormat", SimpleDateFormat.class);

            model.addAttribute("issueDate", printer.format(parser.parse(tl.getListIssueDateTime())));
            try {
                model.addAttribute("nextUpdate", printer.format(parser.parse(tl.getNextUpdate())));
            } catch (NullPointerException exc) {
                model.addAttribute("nextUpdate", "--");
            }

            return "view - trustedList";
        }
        ListOfTrustedLists lotl = serverRetriever.getListOfTrustedLists();
        model.addAttribute("schemeTerritory","EU");
        model.addAttribute("lotl",lotl);
        model.addAttribute("otslp",lotl.getPointersToOtherTsl());
        return "view - listOfTrustedLists";
    }

    @GetMapping("/tlbrowser/{countryCode}/{tspId}/")
    public String trustServiceProviderViewRoute(@PathVariable("countryCode") String countryCode, @PathVariable("tspId") int tspId, HttpServletRequest request, Model model) throws IOException {
        return trustServiceProviderView(countryCode, tspId, request, model);
    }

    @GetMapping("/tlbrowser/{countryCode}/{tspId}")
    public String trustServiceProviderView(@PathVariable("countryCode") String countryCode, @PathVariable("tspId") int tspId, HttpServletRequest request, Model model) throws IOException {

        log.trace("Got request " + request.getRequestURI() + " from " + request.getRemoteAddr());

        TrustedList tl = serverRetriever.getTrustedList(countryCode);
        if (!tl.getIndication().equalsIgnoreCase("TOTAL_PASSED")) {
            log.warn("Trusted list " + countryCode + " validation result: \t Indication: " + tl.getIndication() + "\t Subindication: " + tl.getSubIndication());
            model.addAttribute("indication", tl.getIndication());
            model.addAttribute("subIndication", tl.getSubIndication());
        }
        model.addAttribute("schemeTerritory", tl.getSchemeTerritory());
        model.addAttribute("tspId", tspId);
        model.addAttribute("warnings", tl.getWarnings());
        TrustServiceProvider provider = tl.getTrustServiceProviders().get(tspId);

        model.addAttribute("tsp", provider);

        List<String> serviceProviderQuals = Util.getServiceProviderQuals(provider,true);
        serviceProviderQuals.sort((o1, o2) -> {
            if (o1.startsWith("Q") && !o2.startsWith("Q")) return -1;
            if (!o1.startsWith("Q") && o2.startsWith("Q")) return 1;
            return o1.compareTo(o2);
        });

        model.addAttribute("quals", serviceProviderQuals);
        List<TrustServiceEntry> tss = new ArrayList<>();
        for (int i = 0; i < provider.getTrustServices().size(); i++) {
            TrustService ts = provider.getTrustServices().get(i);
            List<String> quals = Util.getServiceQuals(ts);
            for (String qual : quals) {
                TrustServiceEntry entry = new TrustServiceEntry();
                entry.setId(i);
                entry.setTrustService(ts);
                entry.setQual(qual);
                tss.add(entry);
            }
        }
        tss.sort(Comparator.comparing(o -> o.getTrustService().getServiceName()));
        model.addAttribute("tss", tss);

        return "view - trustedServiceProvider";
    }

    @GetMapping("/tlbrowser/{countryCode}/{tspId}/{serviceId}/")
    public String trustServiceViewRoute(@PathVariable("countryCode") String countryCode, @PathVariable("tspId") int tspId, @PathVariable("serviceId") int serviceId, Model model, HttpServletRequest request) throws IOException, ParseException {
        return trustServiceView(countryCode, tspId, serviceId, model, request);
    }

    @GetMapping("/tlbrowser/{countryCode}/{tspId}/{serviceId}")
    public String trustServiceView(@PathVariable("countryCode") String countryCode, @PathVariable("tspId") int tspId, @PathVariable("serviceId") int serviceId, Model model, HttpServletRequest request) throws IOException, ParseException {
        log.trace("Got request " + request.getRequestURI() + " from " + request.getRemoteAddr());

        TrustedList tl = serverRetriever.getTrustedList(countryCode);
        if (!tl.getIndication().equalsIgnoreCase("TOTAL_PASSED")) {
            log.warn("Trusted list " + countryCode + " validation result: \t Indication: " + tl.getIndication() + "\t Subindication: " + tl.getSubIndication());
            model.addAttribute("indication", tl.getIndication());
            model.addAttribute("subIndication", tl.getSubIndication());
        }

        model.addAttribute("schemeTerritory", tl.getSchemeTerritory());
        model.addAttribute("tspId", tspId);
        model.addAttribute("tspName", tl.getTrustServiceProviders().get(tspId).getName());
        model.addAttribute("serviceId", serviceId);
        model.addAttribute("warnings", tl.getWarnings());

        TrustService ts = tl.getTrustServiceProviders().get(tspId).getTrustServices().get(serviceId);
        model.addAttribute("ts", ts);

        SimpleDateFormat parser = context.getBean("getParserDateTimeFormat", SimpleDateFormat.class);
        SimpleDateFormat printer = context.getBean("getPrinterDateTimeFormat", SimpleDateFormat.class);
        model.addAttribute("statusStartingTime", printer.format(parser.parse(ts.getStatusStartingTime())));
        List<CertificateInfo> infos = new ArrayList<>();
        for (DigitalId id : ts.getDigitalIds()) {
            if (id.getType().equalsIgnoreCase("X509Certificate"))
                infos.add(CertUtils.getCertificateInfo(id.getValue()));
        }
        model.addAttribute("certInfos", infos);
        if (ts.getServiceHistory() != null) {
            List<TrustServiceHistoryEntry> historyEntries = new ArrayList<>();
            for (int i = 0; i < ts.getServiceHistory().size(); i++) {
                TrustServiceHistoryEntry entry = new TrustServiceHistoryEntry();
                entry.setId(i);
                entry.setHistoryInstance(ts.getServiceHistory().get(i));
                historyEntries.add(entry);
            }
            historyEntries.sort(Comparator.comparing(o -> ((TrustServiceHistoryEntry)o).getHistoryInstance().getStatusStartingTime()).reversed());
            model.addAttribute("history", historyEntries);
        } else
            model.addAttribute("history", null);
        return "view - trustedService";
    }

    @GetMapping("/tlbrowser/{countryCode}/{tspId}/{serviceId}/{historyId}/")
    public String trustServiceHistoryViewRoute(@PathVariable("countryCode") String countryCode, @PathVariable("tspId") int tspId, @PathVariable("serviceId") int serviceId, @PathVariable("historyId") int historyId, Model model, HttpServletRequest request) throws IOException {
        return trustServiceHistoryView(countryCode,tspId,serviceId,historyId,model,request);
    }

    @GetMapping("/tlbrowser/{countryCode}/{tspId}/{serviceId}/{historyId}")
    public String trustServiceHistoryView(@PathVariable("countryCode") String countryCode, @PathVariable("tspId") int tspId, @PathVariable("serviceId") int serviceId, @PathVariable("historyId") int historyId, Model model, HttpServletRequest request) throws IOException {
        log.trace("Got request " + request.getRequestURI() + " from " + request.getRemoteAddr());

        TrustedList tl = serverRetriever.getTrustedList(countryCode);
        if (!tl.getIndication().equalsIgnoreCase("TOTAL_PASSED")) {
            log.warn("Trusted list " + countryCode + " validation result: \t Indication: " + tl.getIndication() + "\t Subindication: " + tl.getSubIndication());
            model.addAttribute("indication", tl.getIndication());
            model.addAttribute("subIndication", tl.getSubIndication());
        }

        model.addAttribute("schemeTerritory", tl.getSchemeTerritory());
        model.addAttribute("tspId", tspId);
        model.addAttribute("tspName", tl.getTrustServiceProviders().get(tspId).getName());
        model.addAttribute("serviceId", serviceId);
        model.addAttribute("historyId", historyId);
        model.addAttribute("warnings", tl.getWarnings());

        TrustServiceHistoryInstance instance = tl.getTrustServiceProviders().get(tspId).getTrustServices().get(serviceId).getServiceHistory().get(historyId);
        model.addAttribute("history", instance);

        List<CertificateInfo> infos = new ArrayList<>();
        for (DigitalId id : instance.getDigitalIds()) {
            if (id.getType().equalsIgnoreCase("X509Certificate"))
                infos.add(CertUtils.getCertificateInfo(id.getValue()));
        }
        model.addAttribute("certInfos", infos);

        return "view - trustedServiceHistory";
    }

    @GetMapping("/validateCertificate/")
    public String verifyCertificateRoute(HttpServletRequest request) {
        return verifyCertificate(request);
    }

    @GetMapping("/validateCertificate")
    public String verifyCertificate(HttpServletRequest request) {
        log.trace("Got request " + request.getRequestURI() + " from " + request.getRemoteAddr());

        return "view - verifyCertificate";
    }

    @GetMapping("/validateSignature/")
    public String verifySignatureRoute(HttpServletRequest request) {
        return verifySignature(request);
    }

    @GetMapping("/validateSignature")
    public String verifySignature(HttpServletRequest request) {
        log.trace("Got request " + request.getRequestURI() + " from " + request.getRemoteAddr());

        return "view - verifySignature";
    }
}
