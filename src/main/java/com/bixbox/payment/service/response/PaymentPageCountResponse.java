package com.bixbox.payment.service.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentPageCountResponse {
    private int totalCount; // 총 페이지 수
    private int pageCount; // 만들어지는 페이지 수

    @Builder
    private PaymentPageCountResponse(int totalCount, int pageCount) {
        this.totalCount=totalCount;
        this.pageCount=pageCount;
    }

    public static PaymentPageCountResponse getPaymentPageCountResponse(int totalCount, int pageSize) {
        return PaymentPageCountResponse.builder().totalCount(totalCount).pageCount((int) Math.ceil((double) totalCount / pageSize)).build();
    }
}
