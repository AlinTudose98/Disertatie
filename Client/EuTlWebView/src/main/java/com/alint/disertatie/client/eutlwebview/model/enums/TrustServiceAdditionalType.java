package com.alint.disertatie.client.eutlwebview.model.enums;

public enum TrustServiceAdditionalType implements StringBasedEnum{
    ForESignatures("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForeSignatures"),
    ForESeals("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForeSeals"),
    ForWebSiteAuthentication("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForWebSiteAuthentication"),
    ;

    private String value;

    private TrustServiceAdditionalType(String value) {
        this.value = value;
    }


    @Override
    public String getValue() {
        return value;
    }
}
