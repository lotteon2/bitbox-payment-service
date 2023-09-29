package com.bixbox.payment.util;

import com.bixbox.payment.dto.PaymentDto;
import com.bixbox.payment.exception.KakaoPayArgumentException;
import io.github.bitbox.bitbox.enums.SubscriptionType;

public class EntityValidation {
    // 테스트 코드 작성을 위해서 Custom Validation을 분리하였음
    public static void validPaymentDto(PaymentDto paymentDto){
        SubscriptionType subscriptionType = paymentDto.getSubscriptionType();
        Long chargeCredit = paymentDto.getChargeCredit();
        Long quantity = paymentDto.getQuantity();

        // 1. 크레딧 결제와 구독권 결제가 아닌 경우 혹은 크레딧과 구독권을 동시에 결제하는 경우
        if ((subscriptionType == null && chargeCredit == null) || (subscriptionType != null && chargeCredit != null)) {
            throw new KakaoPayArgumentException("크레딧과 구독권이 둘 다 비어있거나 동시에 결제하려고 할 수 없습니다");
        }

        // 2. subscriptionType이 null이 아니면서 quantity가 1이 아닌 경우
        if (subscriptionType != null && quantity != 1) {
            throw new KakaoPayArgumentException("구독권 결제의 경우 수량은 항상 1이여야 합니다");
        }
    }
}
