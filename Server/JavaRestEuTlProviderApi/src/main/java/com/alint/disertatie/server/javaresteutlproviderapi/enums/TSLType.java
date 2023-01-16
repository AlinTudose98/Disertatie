package com.alint.disertatie.server.javaresteutlproviderapi.enums;

import eu.europa.esig.dss.enumerations.UriBasedEnum;

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
