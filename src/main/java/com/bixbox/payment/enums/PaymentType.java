package com.bixbox.payment.enums;

public enum PaymentType {
    KAKAOPAY(100),
    TOSSPAY(101),
    ACCOUNT(102);

    private final int value;

    private PaymentType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}