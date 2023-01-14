package com.alint.disertatie.client.eutlwebview.model.enums;

public enum TrustServiceStatus implements StringBasedEnum {
    NationalLevelRecognised("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/recognisedatnationallevel"),
    NationalLevelDeprecated("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/deprecatedatnationallevel"),
    Granted("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/granted"),
    Withdrawn("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/withdrawn"),
    UnderSupervision("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/undersupervision"),
    SupervisionInCessation("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/supervisionincessation"),
    SupervisionCeased("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/supervisionceased"),
    SupervisionRevoked("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/supervisionrevoked"),
    Accredited("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/accredited"),
    AccreditationCeased("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/accreditationceased"),
    AccreditationRevoked("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/accreditationrevoked"),
    SetByNationalLaw("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/setbynationallaw"),
    DeprecatedByNationalLaw("http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/deprecatedbynationallaw")
    ;

    private String value;

    private TrustServiceStatus(String value) {
        this.value = value;
    }


    @Override
    public String getValue() {
        return this.value;
    }
}
