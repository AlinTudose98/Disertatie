package com.alint.disertatie.server.javaresteutlproviderapi.enums;

public enum STITrustServiceAdditionalType implements STIStringBasedEnum {
    ForESignatures("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForeSignatures"),
    ForESeals("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForeSeals"),
    ForWebSiteAuthentication("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForWebSiteAuthentication"),
    ;

    private String value;

    private STITrustServiceAdditionalType(String value) {
        this.value = value;
    }


    @Override
    public String getValue() {
        return value;
    }
}
