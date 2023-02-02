package com.alint.disertatie.client.eutlwebview.util;

import com.alint.disertatie.client.eutlwebview.model.entity.STITrustService;
import com.alint.disertatie.client.eutlwebview.model.entity.STITrustServiceProvider;
import com.alint.disertatie.client.eutlwebview.model.enums.STITrustServiceAdditionalType;
import com.alint.disertatie.client.eutlwebview.model.enums.STITrustServiceStatus;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class STIUtil {
    private final static Map<String, String> qualStringMap;
    private final static Map<STITrustServiceStatus,String> statusStringMap;

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
        statusStringMap.put(STITrustServiceStatus.NationalLevelRecognised,"Recognised at national level");
        statusStringMap.put(STITrustServiceStatus.NationalLevelDeprecated,"Deprecated at national level");
        statusStringMap.put(STITrustServiceStatus.Granted,"Granted");
        statusStringMap.put(STITrustServiceStatus.Withdrawn,"Withdrawn");
        statusStringMap.put(STITrustServiceStatus.UnderSupervision,"Under supervision");
        statusStringMap.put(STITrustServiceStatus.SupervisionInCessation,"Supervision in cessation");
        statusStringMap.put(STITrustServiceStatus.SupervisionCeased,"Supervision ceased");
        statusStringMap.put(STITrustServiceStatus.SupervisionRevoked,"Supervision revoked");
        statusStringMap.put(STITrustServiceStatus.Accredited,"Accredited");
        statusStringMap.put(STITrustServiceStatus.AccreditationCeased,"Accreditaion ceased");
        statusStringMap.put(STITrustServiceStatus.AccreditationRevoked,"Accreditation revoked");
        statusStringMap.put(STITrustServiceStatus.SetByNationalLaw,"Set by national law");
        statusStringMap.put(STITrustServiceStatus.DeprecatedByNationalLaw,"Deprecated by national law");
    }


    public static String formatTime(String time) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat printer = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return printer.format(parser.parse(time));
    }
    public static String translateStatus(STITrustServiceStatus status) {
        return statusStringMap.get(status);
    }
    public static String translateQual(String qual) {
        return qualStringMap.get(qual);
    }

    public static List<String> getServiceQuals(STITrustService ts) {
        List<String> quals = new ArrayList<>();

        switch (ts.getServiceType()) {
            case QC_CA, QC_OCSP, QC_CRL -> {
                if (ts.getAdditionalTypes() != null) {
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForESignatures)) {
                        addIfNotExists(quals, "QCert for ESig");
                    }
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForESeals)) {
                        addIfNotExists(quals, "QCert for ESeal");
                    }
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForWebSiteAuthentication)) {
                        addIfNotExists(quals, "QWAC");
                    }
                } else addIfNotExists(quals, "Undefined type");
            }
            case Q_TST -> addIfNotExists(quals, "QTimestamp");
            case Q_QESVAL -> {
                if (ts.getAdditionalTypes() != null) {
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForESignatures)) {
                        addIfNotExists(quals, "QVal for QEsig");
                    }
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForESeals)) {
                        addIfNotExists(quals, "QVal for QESeal");
                    }
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForWebSiteAuthentication)) {
                        addIfNotExists(quals, "QVal for QWAC");
                    }
                } else addIfNotExists(quals, "Undefined type");
            }
            case Q_PSES -> {
                if (ts.getAdditionalTypes() != null) {
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForESignatures)) {
                        addIfNotExists(quals, "QPres for QEsig");
                    }
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForESeals)) {
                        addIfNotExists(quals, "QPres for QESeal");
                    }
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForWebSiteAuthentication)) {
                        addIfNotExists(quals, "QPres for QWAC");
                    }
                } else addIfNotExists(quals, "Undefined type");
            }
            case Q_EDS, Q_REM -> addIfNotExists(quals, "QeRDS");
            case NQC_CA, NQC_OCSP, NQC_CRL -> {
                if (ts.getAdditionalTypes() != null) {
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForESignatures)) {
                        addIfNotExists(quals, "Cert for ESig");
                    }
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForESeals)) {
                        addIfNotExists(quals, "Cert for ESeal");
                    }
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForWebSiteAuthentication)) {
                        addIfNotExists(quals, "WAC");
                    }
                } else addIfNotExists(quals, "Undefined type");
            }
            case NQ_TST, NQ_TSSQC, NQ_TSSADESQCQES -> addIfNotExists(quals, "Timestamp");
            case NQ_EDS, NQ_REMDS -> addIfNotExists(quals, "eRDS");
            case NQ_PSES -> {
                if (ts.getAdditionalTypes() != null) {
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForESignatures)) {
                        addIfNotExists(quals, "Pres for ESig");
                    }
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForESeals)) {
                        addIfNotExists(quals, "Pres for ESeal");
                    }
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForWebSiteAuthentication)) {
                        addIfNotExists(quals, "Pres for WAC");
                    }
                } else addIfNotExists(quals, "Undefined type");
            }
            case NQ_ADESV -> {
                if (ts.getAdditionalTypes() != null) {
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForESignatures)) {
                        addIfNotExists(quals, "Val for ESig");
                    }
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForESeals)) {
                        addIfNotExists(quals, "Val for ESeal");
                    }
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForWebSiteAuthentication)) {
                        addIfNotExists(quals, "Val for WAC");
                    }
                } else addIfNotExists(quals, "Undefined type");
            }
            case NQ_ADESG -> {
                if (ts.getAdditionalTypes() != null) {
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForESignatures)) {
                        addIfNotExists(quals, "Gen for ESig");
                    }
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForESeals)) {
                        addIfNotExists(quals, "Gen for ESeal");
                    }
                    if (ts.getAdditionalTypes().contains(STITrustServiceAdditionalType.ForWebSiteAuthentication)) {
                        addIfNotExists(quals, "Gen for WAC");
                    }
                } else addIfNotExists(quals, "Undefined type");
            }
            default -> addIfNotExists(quals, "Non-Regulatory");

        }
        return quals;
    }

    public static List<String> getServiceProviderQuals(STITrustServiceProvider tsp, boolean getAll) {
        List<String> quals = new ArrayList<>();
        for(STITrustService ts : tsp.getTrustServices()) {
            if(getAll) {
                addAllIfNotExists(quals,getServiceQuals(ts));
            }
            else {
                if (ts.getServiceStatus().equals(STITrustServiceStatus.Granted) || ts.getServiceStatus().equals(STITrustServiceStatus.NationalLevelRecognised)) {
                    addAllIfNotExists(quals,getServiceQuals(ts));
                }
            }
        }
        return quals;
    }
    public static List<String> getServiceProviderQuals(STITrustServiceProvider tsp) {
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

    public static String cleanBase64Input(String base64) {
        base64 = base64.replace("\n","");
        base64 = base64.replace("-----BEGIN CERTIFICATE-----", "");
        base64 = base64.replace("-----END CERTIFICATE-----","");

        return base64;
    }
}
