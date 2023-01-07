package com.alint.disertatie.javaeutlclient.model.enums;

public enum TSLType implements StringBasedEnum {
    EUListOfTheLists("http://uri.etsi.org/TrstSvc/TrustedList/TSLType/EUlistofthelists"),
    EUGeneric("http://uri.etsi.org/TrstSvc/TrustedList/TSLType/EUgeneric"),
    GenericNonEu(""),
    OTHER("http://uri.etsi.org/TrstSvc/TrustedList/TSLType/CClist")
    ;

    private final String value;

    private TSLType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
