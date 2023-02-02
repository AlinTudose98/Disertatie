package com.alint.disertatie.server.javaresteutlproviderapi.exception;

public class STITrustedListNotParsedException extends STICustomException {
    public STITrustedListNotParsedException() {
    }

    public STITrustedListNotParsedException(String message) {
        super(message);
    }

    public STITrustedListNotParsedException(String message, Throwable cause) {
        super(message, cause);
    }

    public STITrustedListNotParsedException(Throwable cause) {
        super(cause);
    }

    public STITrustedListNotParsedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
