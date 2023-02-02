package com.alint.disertatie.server.javaresteutlproviderapi.exception;

public class STICustomException extends RuntimeException{
    public STICustomException() {
        super();
    }

    public STICustomException(String message) {
        super(message);
    }

    public STICustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public STICustomException(Throwable cause) {
        super(cause);
    }

    protected STICustomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
