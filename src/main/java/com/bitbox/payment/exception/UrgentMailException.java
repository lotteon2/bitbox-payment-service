package com.bitbox.payment.exception;

public class UrgentMailException extends RuntimeException{
    public UrgentMailException() {
        super();
    }

    public UrgentMailException(String message) {
        super(message);
    }

    public UrgentMailException(String message, Throwable cause) {
        super(message, cause);
    }

    public UrgentMailException(Throwable cause) {
        super(cause);
    }

    protected UrgentMailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
