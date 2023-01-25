package com.alint.disertatie.client.eutlwebview.util;

import com.alint.disertatie.client.eutlwebview.model.entity.TrustService;
import com.alint.disertatie.client.eutlwebview.model.entity.TrustServiceProvider;
import com.alint.disertatie.client.eutlwebview.model.enums.TrustServiceAdditionalType;
import com.alint.disertatie.client.eutlwebview.model.enums.TrustServiceStatus;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class Util {
    private final static Map<String, String> qualStringMap;
    private final static Map<TrustServiceStatus,String> statusStringMap;

    static {
        qualStringMap = new HashMap<>();
        qualStringMap.put("QCert for ESig", "Qualified certificate for electronic signature");
        qualStringMap.put("QCert for ESeal", "Qualified certificate for electronic seal");
        qualStringMap.put("QWAC", "Qualified certificate for website authentication");
        qualStringMap.put("QTimestamp", "Qualified time stamp");
        qualStringMap.put("QVal for QEsig", "Qualified validation service for qualified electronic signature");
        qualStringMap.put("QVal for QESeal", "Qualified validation service for qualified electronic seal");
        qualStringMap.put("QVal for QWAC", "Qualified validation service for qualified website authentication certificates");
        qualStringMap.put("QPres for QEsig", "Qualified preservation service for qualified electronic signature");
        qualStringMap.put("QPres for QESeal", "Qualified preservation service for qualified electronic seal");
        qualStringMap.put("QPres for QWAC", "Qualified preservation service for qualified website authentication certificates");
        qualStringMap.put("QeRDS", "Qualified electronic registrered delivery service");
        qualStringMap.put("Cert for ESig", "Certificate for electronic signature");
        qualStringMap.put("Cert for ESeal", "Certificate for electronic seal");
        qualStringMap.put("WAC", "Certificate for website authentication");
        qualStringMap.put("Timestamp", "Time stamp service");
        qualStringMap.put("eRDS", "Electronic registrered delivery service");
        qualStringMap.put("Pres for Esig", "Preservation service for electronic signature");
        qualStringMap.put("Pres for ESeal", "Preservation service for electronic seal");
        qualStringMap.put("Pres for WAC", "Preservation service for website authentication certificates");
        qualStringMap.put("Val for ESig", "Validation service for electronic signature");
        qualStringMap.put("Val for ESeal", "Validation service for electronic seal");
        qualStringMap.put("Val for WAC", "Validation service for website authentication certificates");
        qualStringMap.put("Gen for ESig", "Generation service for electronic signature");
        qualStringMap.put("Gen for ESeal", "Generation service for electronic seal");
        qualStringMap.put("Gen for WAC", "Generation service for website authentication certificates");
        qualStringMap.put("Undefined type", "Undefined type");
        qualStringMap.put("Non-Regulatory", "Non-regulatory, nationally defined trust service");

        statusStringMap = new HashMap<>();
        statusStringMap.put(TrustServiceStatus.NationalLevelRecognised,"Recognised at national level");
        statusStringMap.put(TrustServiceStatus.NationalLevelDeprecated,"Deprecated at national level");
        statusStringMap.put(TrustServiceStatus.Granted,"Granted");
        statusStringMap.put(TrustServiceStatus.Withdrawn,"Withdrawn");
        statusStringMap.put(TrustServiceStatus.UnderSupervision,"Under supervision");
        statusStringMap.put(TrustServiceStatus.SupervisionInCessation,"Supervision in cessation");
        statusStringMap.put(TrustServiceStatus.SupervisionCeased,"Supervision ceased");
        statusStringMap.put(TrustServiceStatus.SupervisionRevoked,"Supervision revoked");
        statusStringMap.put(TrustServiceStatus.Accredited,"Accredited");
        statusStringMap.put(TrustServiceStatus.AccreditationCeased,"Accreditaion ceased");
        statusStringMap.put(TrustServiceStatus.AccreditationRevoked,"Accreditation revoked");
        statusStringMap.put(TrustServiceStatus.SetByNationalLaw,"Set by national law");
        statusStringMap.put(TrustServiceStatus.DeprecatedByNationalLaw,"Deprecated by national law");
    }


    public static String formatTime(String time) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat printer = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return printer.format(parser.parse(time));
    }
    public static String translateStatus(TrustServiceStatus status) {
        return statusStringMap.get(status);
    }
    public static String translateQual(String qual) {
        return qualStringMap.get(qual);
    }

    public static List<String> getServiceQuals(TrustService ts) {
        List<String> quals = new ArrayList<>();

        switch (ts.getServiceType()) {
            case QC_CA, QC_OCSP, QC_CRL -> {
                if (ts.getAdditionalTypes() != null) {
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESignatures)) {
                        addIfNotExists(quals, "QCert for ESig");
                    }
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESeals)) {
                        addIfNotExists(quals, "QCert for ESeal");
                    }
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForWebSiteAuthentication)) {
                        addIfNotExists(quals, "QWAC");
                    }
                } else addIfNotExists(quals, "Undefined type");
            }
            case Q_TST -> addIfNotExists(quals, "QTimestamp");
            case Q_QESVAL -> {
                if (ts.getAdditionalTypes() != null) {
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESignatures)) {
                        addIfNotExists(quals, "QVal for QEsig");
                    }
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESeals)) {
                        addIfNotExists(quals, "QVal for QESeal");
                    }
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForWebSiteAuthentication)) {
                        addIfNotExists(quals, "QVal for QWAC");
                    }
                } else addIfNotExists(quals, "Undefined type");
            }
            case Q_PSES -> {
                if (ts.getAdditionalTypes() != null) {
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESignatures)) {
                        addIfNotExists(quals, "QPres for QEsig");
                    }
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESeals)) {
                        addIfNotExists(quals, "QPres for QESeal");
                    }
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForWebSiteAuthentication)) {
                        addIfNotExists(quals, "QPres for QWAC");
                    }
                } else addIfNotExists(quals, "Undefined type");
            }
            case Q_EDS, Q_REM -> addIfNotExists(quals, "QeRDS");
            case NQC_CA, NQC_OCSP, NQC_CRL -> {
                if (ts.getAdditionalTypes() != null) {
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESignatures)) {
                        addIfNotExists(quals, "Cert for ESig");
                    }
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESeals)) {
                        addIfNotExists(quals, "Cert for ESeal");
                    }
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForWebSiteAuthentication)) {
                        addIfNotExists(quals, "WAC");
                    }
                } else addIfNotExists(quals, "Undefined type");
            }
            case NQ_TST, NQ_TSSQC, NQ_TSSADESQCQES -> addIfNotExists(quals, "Timestamp");
            case NQ_EDS, NQ_REMDS -> addIfNotExists(quals, "eRDS");
            case NQ_PSES -> {
                if (ts.getAdditionalTypes() != null) {
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESignatures)) {
                        addIfNotExists(quals, "Pres for ESig");
                    }
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESeals)) {
                        addIfNotExists(quals, "Pres for ESeal");
                    }
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForWebSiteAuthentication)) {
                        addIfNotExists(quals, "Pres for WAC");
                    }
                } else addIfNotExists(quals, "Undefined type");
            }
            case NQ_ADESV -> {
                if (ts.getAdditionalTypes() != null) {
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESignatures)) {
                        addIfNotExists(quals, "Val for ESig");
                    }
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESeals)) {
                        addIfNotExists(quals, "Val for ESeal");
                    }
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForWebSiteAuthentication)) {
                        addIfNotExists(quals, "Val for WAC");
                    }
                } else addIfNotExists(quals, "Undefined type");
            }
            case NQ_ADESG -> {
                if (ts.getAdditionalTypes() != null) {
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESignatures)) {
                        addIfNotExists(quals, "Gen for ESig");
                    }
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESeals)) {
                        addIfNotExists(quals, "Gen for ESeal");
                    }
                    if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForWebSiteAuthentication)) {
                        addIfNotExists(quals, "Gen for WAC");
                    }
                } else addIfNotExists(quals, "Undefined type");
            }
            default -> addIfNotExists(quals, "Non-Regulatory");

        }
        return quals;
    }

    public static List<String> getServiceProviderQuals(TrustServiceProvider tsp,boolean getAll) {
        List<String> quals = new ArrayList<>();
        for(TrustService ts : tsp.getTrustServices()) {
            if(getAll) {
                addAllIfNotExists(quals,getServiceQuals(ts));
            }
            else {
                if (ts.getServiceStatus().equals(TrustServiceStatus.Granted) || ts.getServiceStatus().equals(TrustServiceStatus.NationalLevelRecognised)) {
                    addAllIfNotExists(quals,getServiceQuals(ts));
                }
            }
        }
        return quals;
    }
    public static List<String> getServiceProviderQuals(TrustServiceProvider tsp) {
        return getServiceProviderQuals(tsp,false);
    }


    public static <T> void addAllIfNotExists(List<T> dest,List<T> src) {
        for (T obj: src) {
            addIfNotExists(dest,obj);
        }
    }
    public static <T> void addIfNotExists(List<T> list, T obj) {
        if (!list.contains(obj)) {
            list.add(obj);
        }
    }
}
