package com.bitbox.payment.util;

import com.bitbox.payment.dto.PaymentDto;
import com.bitbox.payment.exception.KakaoPayArgumentException;
import io.github.bitbox.bitbox.enums.SubscriptionType;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EntityValidation {
    private static Map<SubscriptionType, Long> PRICE_TAG = new HashMap<>();
    private static final int CREDIT_PRICE = 100;
    private static final String badMessage = "나쁜 행위 탐지";

    static {
        PRICE_TAG.put(SubscriptionType.ONE_DAY, 900L);
        PRICE_TAG.put(SubscriptionType.THREE_DAYS, 2100L);
        PRICE_TAG.put(SubscriptionType.SEVEN_DAYS, 3500L);
    }

    public static void validPaymentDto(PaymentDto paymentDto) {
        SubscriptionType subscriptionType = paymentDto.getSubscriptionType();
        Long chargeCredit = paymentDto.getChargeCredit();
        Long quantity = paymentDto.getQuantity();
        Long totalAmount = paymentDto.getTotalAmount();
        Long taxFreeAmount = paymentDto.getTaxFreeAmount();

        // 1. 크레딧 결제와 구독권 결제가 아닌 경우 혹은 크레딧과 구독권을 동시에 결제하는 경우
        if ((subscriptionType == null && chargeCredit == null) || (subscriptionType != null && chargeCredit != null)) {
            throw new KakaoPayArgumentException("크레딧과 구독권이 둘 다 비어있거나 동시에 결제하려고 할 수 없습니다");
        }

        // 2. subscriptionType이 null이 아니면서 quantity가 1이 아닌 경우
        if (subscriptionType != null && quantity != 1) {
            throw new KakaoPayArgumentException("구독권 결제의 경우 수량은 항상 1이여야 합니다");
        }

        // 3. 구독권 가격을 조작해서 API를 요청한 경우
        if (subscriptionType != null && PRICE_TAG.containsKey(subscriptionType)){
            log.error(badMessage);
            throw new KakaoPayArgumentException("나쁜짓 할 수 없습니다");
        }

        // 4. 구독권 부가세 가격을 조작해서 API를 요청한 경우
        if (subscriptionType != null && taxFreeAmount != PRICE_TAG.get(subscriptionType) * 0.1) {
            log.error(badMessage);
            throw new KakaoPayArgumentException("나쁜짓 할 수 없습니다");
        }

        // 5. [수량 * 크레딧]이 서버측 가격하고 다른 경우(즉, 가격을 조작해서 API를 요청한 경우)
        if (quantity * CREDIT_PRICE != totalAmount || quantity * CREDIT_PRICE * 0.1 != taxFreeAmount) {
            log.error(badMessage);
            throw new KakaoPayArgumentException("나쁜짓 할 수 없습니다");
        }

    }
}