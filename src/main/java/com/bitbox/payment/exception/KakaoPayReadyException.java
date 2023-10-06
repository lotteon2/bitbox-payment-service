package com.bitbox.payment.exception;

public class KakaoPayReadyException extends RuntimeException{
    public KakaoPayReadyException() {
    }

    public KakaoPayReadyException(String message) {
        super(message);
    }

    public KakaoPayReadyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KakaoPayReadyException(Throwable cause) {
        super(cause);
    }

    public KakaoPayReadyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
