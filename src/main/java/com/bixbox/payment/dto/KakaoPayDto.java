package com.bixbox.payment.dto;

import io.github.bitbox.bitbox.enums.SubscriptionType;
import lombok.Getter;

@Getter
public class KakaoPayDto {
    private String cid;
    private String tid;
    private String itemName;
    private Long amount;
    private Long taxFreeAmount;
    private String partnerOrderId;
    private String partnerUserId;
    private String pgToken;
    private SubscriptionType subscription;
    private Long credit;

    public void setPgToken(String pgToken) {
        this.pgToken = pgToken;
    }
}