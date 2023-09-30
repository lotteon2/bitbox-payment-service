package com.bixbox.payment.service.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentPageCountResponse {
    private int totalPages; // 총 페이지 수
    private int pageCount; // 만들어지는 페이지 수

    @Builder
    private PaymentPageCountResponse(int totalPages, int pageCount) {
        this.totalPages=totalPages;
        this.pageCount=pageCount;
    }

    public static PaymentPageCountResponse getPaymentPageCountResponse(int totalCount, int pageSize) {
        return PaymentPageCountResponse.builder().totalPages(totalCount).pageCount((int) Math.ceil((double) totalCount / pageSize)).build();
    }
}
