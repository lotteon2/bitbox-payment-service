package com.bixbox.payment.exception;

public class KakaoPayArgumentException extends RuntimeException{
    public KakaoPayArgumentException() {
    }

    public KakaoPayArgumentException(String message) {
        super(message);
    }

    public KakaoPayArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public KakaoPayArgumentException(Throwable cause) {
        super(cause);
    }

    public KakaoPayArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
