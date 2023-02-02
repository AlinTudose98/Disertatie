package com.alint.disertatie.server.javaresteutlproviderapi.exception;

public class STIBadRequestException extends STICustomException {
    public STIBadRequestException() {
    }

    public STIBadRequestException(String message) {
        super(message);
    }

    public STIBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public STIBadRequestException(Throwable cause) {
        super(cause);
    }

    public STIBadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
