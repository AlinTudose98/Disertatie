package com.alint.disertatie.client.eutlwebview.util;

import com.alint.disertatie.client.eutlwebview.model.entity.TrustService;
import com.alint.disertatie.client.eutlwebview.model.entity.TrustServiceProvider;
import com.alint.disertatie.client.eutlwebview.model.enums.TrustServiceAdditionalType;
import com.alint.disertatie.client.eutlwebview.model.enums.TrustServiceStatus;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static List<String> getServiceProviderQuals(TrustServiceProvider tsp) {
        List<String> quals = new ArrayList<>();
        for (TrustService ts : tsp.getTrustServices()) {
            if (ts.getServiceStatus().equals(TrustServiceStatus.Granted)) {
                switch (ts.getServiceType()) {
                    case QC_CA, QC_OCSP, QC_CRL -> {
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESignatures)) {
                            addIfNotExists(quals, "QCert for ESig");
                        }
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESeals)) {
                            addIfNotExists(quals, "QCert for ESeal");
                        }
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForWebSiteAuthentication)) {
                            addIfNotExists(quals, "QWAC");
                        }
                    }
                    case Q_TST -> addIfNotExists(quals, "QTimestamp");
                    case Q_QESVAL -> {
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESignatures)) {
                            addIfNotExists(quals, "QVal for QEsig");
                        }
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESeals)) {
                            addIfNotExists(quals, "QVal for QESeal");
                        }
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForWebSiteAuthentication)) {
                            addIfNotExists(quals, "QVal for QWAC");
                        }
                    }
                    case Q_PSES -> {
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESignatures)) {
                            addIfNotExists(quals, "QPres for QEsig");
                        }
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESeals)) {
                            addIfNotExists(quals, "QPres for QESeal");
                        }
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForWebSiteAuthentication)) {
                            addIfNotExists(quals, "QPres for QWAC");
                        }
                    }
                    case Q_EDS, Q_REM -> addIfNotExists(quals, "QeRDS");
                    default -> addIfNotExists(quals,"Non-Regulatory");
                }
            }
            if(ts.getServiceStatus().equals(TrustServiceStatus.NationalLevelRecognised)) {
                switch (ts.getServiceType()) {
                    case NQC_CA,NQC_OCSP,NQC_CRL -> {
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESignatures)) {
                            addIfNotExists(quals,"Cert for ESig");
                        }
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESeals)) {
                            addIfNotExists(quals,"Cert for ESeal");
                        }
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForWebSiteAuthentication)) {
                            addIfNotExists(quals,"WAC");
                        }
                    }
                    case NQ_TST,NQ_TSSQC,NQ_TSSADESQCQES -> addIfNotExists(quals, "Timestamp");
                    case NQ_EDS,NQ_REMDS -> addIfNotExists(quals,"eRDS");
                    case NQ_PSES -> {
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESignatures)) {
                            addIfNotExists(quals, "Pres for Esig");
                        }
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESeals)) {
                            addIfNotExists(quals, "Pres for ESeal");
                        }
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForWebSiteAuthentication)) {
                            addIfNotExists(quals, "Pres for WAC");
                        }
                    }
                    case NQ_ADESV -> {
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESignatures)) {
                            addIfNotExists(quals, "Val for Esig");
                        }
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESeals)) {
                            addIfNotExists(quals, "Val for ESeal");
                        }
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForWebSiteAuthentication)) {
                            addIfNotExists(quals, "Val for WAC");
                        }
                    }
                    case NQ_ADESG -> {
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESignatures)) {
                            addIfNotExists(quals, "Gen for Esig");
                        }
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForESeals)) {
                            addIfNotExists(quals, "Gen for ESeal");
                        }
                        if (ts.getAdditionalTypes().contains(TrustServiceAdditionalType.ForWebSiteAuthentication)) {
                            addIfNotExists(quals, "Gen for WAC");
                        }
                    }
                    default -> addIfNotExists(quals,"Non-Regulatory");

                }
            }
        }

        return quals;
    }


    public static <T> void addIfNotExists(List<T> list, T obj) {
        if (!list.contains(obj)) {
            list.add(obj);
        }
    }
}
