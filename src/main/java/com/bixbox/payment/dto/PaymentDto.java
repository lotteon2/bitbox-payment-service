package com.bixbox.payment.dto;

import io.github.bitbox.bitbox.enums.SubscriptionType;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Builder
public class PaymentDto {
    @NotEmpty(message = "파트너 유저 아이디 비어있을 수 없습니다.")
    private String partnerUserId; // DB에 따로 저장을 안함(랜덤 UUID)
    @NotEmpty(message = "파트너 주문 번호는 비어있을 수 없습니다.")
    private String partnerOrderId; // DB에 따로 저장을 안함(랜덤 UUID)
    @NotEmpty(message = "아이템명은 비어있을 수 없습니다.")
    private String itemName;
    @NotNull(message = "수량은 비어있을 수 없습니다.")
    private Long quantity;
    @NotNull(message = "총금액은 비어있을 수 없습니다.")
    private Long totalAmount;
    @NotNull(message = "비과세는 비어있을 수 없습니다.")
    private Long taxFreeAmount;
    private SubscriptionType subscriptionType;
    private Long chargeCredit;
}