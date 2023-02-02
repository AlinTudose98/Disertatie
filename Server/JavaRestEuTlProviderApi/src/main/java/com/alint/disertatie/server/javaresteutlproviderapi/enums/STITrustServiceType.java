package com.alint.disertatie.server.javaresteutlproviderapi.enums;

public enum STITrustServiceType implements STIStringBasedEnum {
    // Follows qualified service providers
    QC_CA("http://uri.etsi.org/TrstSvc/Svctype/CA/QC"),
    QC_OCSP("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP/QC"),
    QC_CRL("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/CRL/QC"),
    Q_TST("http://uri.etsi.org/TrstSvc/Svctype/TSA/QTST"),
    Q_EDS("http://uri.etsi.org/TrstSvc/Svctype/EDS/Q"),
    Q_REM("http://uri.etsi.org/TrstSvc/Svctype/EDS/REM/Q"),
    Q_PSES("http://uri.etsi.org/TrstSvc/Svctype/PSES/Q"),
    Q_QESVAL("http://uri.etsi.org/TrstSvc/Svctype/QESValidation/Q"),
    // Follows not qualified service providers
    NQC_CA("http://uri.etsi.org/TrstSvc/Svctype/CA/PKC"),
    NQC_OCSP("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/OCSP"),
    NQC_CRL("http://uri.etsi.org/TrstSvc/Svctype/Certstatus/CRL"),
    NQ_TST("http://uri.etsi.org/TrstSvc/Svctype/TSA"),
    NQ_TSSQC("http://uri.etsi.org/TrstSvc/Svctype/TSA/TSS-QC"),
    NQ_TSSADESQCQES("http://uri.etsi.org/TrstSvc/Svctype/TSA/TSS-AdESQCandQES"),
    NQ_EDS("http://uri.etsi.org/TrstSvc/Svctype/EDS"),
    NQ_REMDS("http://uri.etsi.org/TrstSvc/Svctype/EDS/REM"),
    NQ_PSES("http://uri.etsi.org/TrstSvc/Svctype/PSES"),
    NQ_ADESV("http://uri.etsi.org/TrstSvc/Svctype/AdESValidation"),
    NQ_ADESG("http://uri.etsi.org/TrstSvc/Svctype/AdESGeneration"),
    // Follows not qualified, national level service providers
    NAT_RA("http://uri.etsi.org/TrstSvc/Svctype/RA"),
    NAT_RANOPKI("http://uri.etsi.org/TrstSvc/Svctype/RA/nothavingPKIid"),
    NAT_ACA("http://uri.etsi.org/TrstSvc/Svctype/ACA"),
    NAT_SPA("http://uri.etsi.org/TrstSvc/Svctype/SignaturePolicyAuthority"),
    NAT_ARCH("http://uri.etsi.org/TrstSvc/Svctype/Archiv"),
    NAT_ARCHNOPKI("http://uri.etsi.org/TrstSvc/Svctype/Archiv/nothavingPKIid"),
    NAT_IDV("http://uri.etsi.org/TrstSvc/Svctype/IdV"),
    NAT_IDVNOPKI("http://uri.etsi.org/TrstSvc/Svctype/IdV/nothavingPKIid"),
    NAT_QESC("http://uri.etsi.org/TrstSvc/Svctype/KEscrow"),
    NAT_QESCNOPKI("http://uri.etsi.org/TrstSvc/Svctype/KEscrow/nothavingPKIid"),
    NAT_PP("http://uri.etsi.org/TrstSvc/Svctype/PPwd"),
    NAT_PPNOPKI("http://uri.etsi.org/TrstSvc/Svctype/PPwd/nothavingPKIid"),
    NAT_TLISS("http://uri.etsi.org/TrstSvd/Svctype/TLIssuer"),
    NAT_ROOTCA_QC("http://uri.etsi.org/TrstSvc/Svctype/NationalRootCA-QC"),

    UNSPECIFIED("http://uri.etsi.org/TrstSvc/Svctype/unspecified");
    private final String value;

    STITrustServiceType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
