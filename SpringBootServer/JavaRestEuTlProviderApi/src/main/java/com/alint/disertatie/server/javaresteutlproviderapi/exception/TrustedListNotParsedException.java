package com.alint.disertatie.server.javaresteutlproviderapi.exception;

public class TrustedListNotParsedException extends CustomException{
    public TrustedListNotParsedException() {
    }

    public TrustedListNotParsedException(String message) {
        super(message);
    }

    public TrustedListNotParsedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrustedListNotParsedException(Throwable cause) {
        super(cause);
    }

    public TrustedListNotParsedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
