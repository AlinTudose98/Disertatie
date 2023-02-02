package com.alint.disertatie.server.javaresteutlproviderapi.exception;

public class STICountryNotFoundException extends STICustomException {
    public STICountryNotFoundException() {
        super();
    }

    public STICountryNotFoundException(String message) {
        super(message);
    }

    public STICountryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public STICountryNotFoundException(Throwable cause) {
        super(cause);
    }

    protected STICountryNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
