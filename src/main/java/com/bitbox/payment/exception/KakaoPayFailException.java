package com.bitbox.payment.exception;

public class KakaoPayFailException extends RuntimeException{
    public KakaoPayFailException() {
    }

    public KakaoPayFailException(String message) {
        super(message);
    }

    public KakaoPayFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public KakaoPayFailException(Throwable cause) {
        super(cause);
    }

    public KakaoPayFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
