package com.bixbox.payment.enums;

public enum PaymentType {
    KAKAOPAY(100),
    TOSSPAY(101),
    ACCOUNT(102);

    private final int code;

    PaymentType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}