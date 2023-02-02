package com.alint.disertatie.server.javaresteutlproviderapi.enums;

public enum STIMimeType implements STIStringBasedEnum {
    XML("application/vnd.etsi.tsl+xml"),
    PDF("application/pdf")
    ;


    private String value;

    private STIMimeType(String value) {
        this.value = value;
    }


    @Override
    public String getValue() {
        return this.value;
    }
}
