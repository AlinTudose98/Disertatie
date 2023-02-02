package com.alint.disertatie.server.javaresteutlproviderapi.enums;

public enum STITSLType implements STIStringBasedEnum {
    EUListOfTheLists("http://uri.etsi.org/TrstSvc/TrustedList/TSLType/EUlistofthelists"),
    EUGeneric("http://uri.etsi.org/TrstSvc/TrustedList/TSLType/EUgeneric"),
    GenericNonEu(""),
    OTHER("http://uri.etsi.org/TrstSvc/TrustedList/TSLType/CClist")
    ;

    private final String value;

    private STITSLType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
