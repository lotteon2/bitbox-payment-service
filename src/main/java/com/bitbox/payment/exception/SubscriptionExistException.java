package com.bitbox.payment.exception;

public class SubscriptionExistException extends RuntimeException{
    public SubscriptionExistException() {
    }

    public SubscriptionExistException(String message) {
        super(message);
    }

    public SubscriptionExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubscriptionExistException(Throwable cause) {
        super(cause);
    }

    public SubscriptionExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
