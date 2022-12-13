package com.alint.disertatie.server.javaresteutlproviderapi.enums;

public enum MimeType implements StringBasedEnum{
    XML("application/vnd.etsi.tsl+xml"),
    PDF("application/pdf")
    ;


    private String value;

    private MimeType(String value) {
        this.value = value;
    }


    @Override
    public String getValue() {
        return this.value;
    }
}
