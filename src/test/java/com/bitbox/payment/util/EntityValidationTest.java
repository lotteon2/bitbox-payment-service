package com.bitbox.payment.util;

import com.bitbox.payment.dto.PaymentDto;
import com.bitbox.payment.exception.KakaoPayArgumentException;
import io.github.bitbox.bitbox.enums.SubscriptionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class EntityValidationTest {
    @Test
    public void 크레딧_결제와_구독권_결제가_둘다_아닌경우_KakaoPayArgumentException_예외가_발생한다(){
        PaymentDto paymentDto = PaymentDto.builder().chargeCredit(null).subscriptionType(null).build();
        assertThrows(KakaoPayArgumentException.class, () -> EntityValidation.validPaymentDto(paymentDto));
    }

    @Test
    public void 크레딧결제와_구독권정보가_둘다존재하면_KakaoPayArgumentException_예외가_발생한다(){
        PaymentDto paymentDto = PaymentDto.builder().chargeCredit(10L).subscriptionType(SubscriptionType.ONE_DAY).build();
        assertThrows(KakaoPayArgumentException.class, () -> EntityValidation.validPaymentDto(paymentDto));
    }

    @Test
    public void 크레딧만존재하고_구독권정보가_존재하지않으면_정상처리된다(){
        PaymentDto paymentDto = PaymentDto.builder().chargeCredit(10L).subscriptionType(null).totalAmount(1000L).taxFreeAmount(100L).build();
        EntityValidation.validPaymentDto(paymentDto);
    }

    @Test
    public void 구독권만존재하고_크레딧정보가_존재하지않는데_수량이1이아닌경우_KakaoPayArgumentException_예외가_발생한다(){
        PaymentDto paymentDto = PaymentDto.builder().chargeCredit(null).subscriptionType(SubscriptionType.ONE_DAY).quantity(2L).build();
        assertThrows(KakaoPayArgumentException.class, () -> EntityValidation.validPaymentDto(paymentDto));
    }

    @Test
    public void 구독권만존재하고_크레딧정보가_존재하지않는데_수량이1이면_정상처리된다(){
        PaymentDto paymentDto = PaymentDto.builder().chargeCredit(null).subscriptionType(SubscriptionType.ONE_DAY).totalAmount(900L).taxFreeAmount(90L).quantity(1L).build();
        EntityValidation.validPaymentDto(paymentDto);
    }

    @Test
    public void 구독권_1일을_결제하는데_잘못된_금액을_보내는_경우_KakaoPayArgumentException_예외가_발생하고_올바른_금액을_보내는_경우_정상처리된다(){
        callBadRequest(SubscriptionType.ONE_DAY);

        EntityValidation.validPaymentDto(PaymentDto.builder()
                .chargeCredit(null)
                .subscriptionType(SubscriptionType.ONE_DAY)
                .totalAmount(900L)
                .taxFreeAmount(90L)
                .quantity(1L)
                .build());
    }

    @Test
    public void 구독권_1일을_결제하는데_잘못된_부가금액을_보내는_경우_KakaoPayArgumentException_예외가_발생한다(){
        assertThrows(KakaoPayArgumentException.class, () -> EntityValidation.validPaymentDto(PaymentDto.builder()
                .chargeCredit(null)
                .subscriptionType(SubscriptionType.ONE_DAY)
                .totalAmount(900L)
                .taxFreeAmount(10L)
                .quantity(1L)
                .build()));
    }

    @Test
    public void 구독권_3일을_결제하는데_잘못된_금액을_보내는_경우_KakaoPayArgumentException_예외가_발생하고_올바른_금액을_보내는_경우_정상처리된다(){
        callBadRequest(SubscriptionType.THREE_DAYS);

        EntityValidation.validPaymentDto(PaymentDto.builder()
                .chargeCredit(null)
                .subscriptionType(SubscriptionType.THREE_DAYS)
                .totalAmount(2100L)
                .taxFreeAmount(210L)
                .quantity(1L)
                .build());
    }

    @Test
    public void 구독권_3일을_결제하는데_잘못된_부가금액을_보내는_경우_KakaoPayArgumentException_예외가_발생한다(){
        assertThrows(KakaoPayArgumentException.class, () -> EntityValidation.validPaymentDto(PaymentDto.builder()
                .chargeCredit(null)
                .subscriptionType(SubscriptionType.ONE_DAY)
                .totalAmount(2100L)
                .taxFreeAmount(110L)
                .quantity(1L)
                .build()));
    }

    @Test
    public void 구독권_7일을_결제하는데_잘못된_금액을_보내는_경우_KakaoPayArgumentException_예외가_발생하고_올바른_금액을_보내는_경우_정상처리된다(){
        callBadRequest(SubscriptionType.SEVEN_DAYS);

        EntityValidation.validPaymentDto(PaymentDto.builder()
                .chargeCredit(null)
                .subscriptionType(SubscriptionType.SEVEN_DAYS)
                .totalAmount(3500L)
                .taxFreeAmount(350L)
                .quantity(1L)
                .build());
    }

    @Test
    public void 구독권_7일을_결제하는데_잘못된_부가금액을_보내는_경우_KakaoPayArgumentException_예외가_발생한다(){
        assertThrows(KakaoPayArgumentException.class, () -> EntityValidation.validPaymentDto(PaymentDto.builder()
                .chargeCredit(null)
                .subscriptionType(SubscriptionType.SEVEN_DAYS)
                .totalAmount(3500L)
                .taxFreeAmount(330L)
                .quantity(1L)
                .build()));
    }

    @Test
    public void 크레딧을_구매할때_잘못된_금액을_보내는_경우_KakaoPayArgumentException_예외가_발생한다(){
        assertThrows(KakaoPayArgumentException.class, () -> EntityValidation.validPaymentDto(PaymentDto.builder()
                .chargeCredit(10L)
                .subscriptionType(null)
                .totalAmount(3300L)
                .taxFreeAmount(330L)
                .quantity(1L)
                .build()));
    }

    @Test
    public void 크레딧을_구매할때_잘못된_부가금액을_보내는_경우_KakaoPayArgumentException_예외가_발생한다(){
        assertThrows(KakaoPayArgumentException.class, () -> EntityValidation.validPaymentDto(PaymentDto.builder()
                .chargeCredit(10L)
                .subscriptionType(null)
                .totalAmount(1000L)
                .taxFreeAmount(330L)
                .quantity(1L)
                .build()));
    }


    private static void callBadRequest(SubscriptionType subscriptionType) {
        assertThrows(KakaoPayArgumentException.class, () -> EntityValidation.validPaymentDto(PaymentDto.builder()
                .chargeCredit(null)
                .subscriptionType(subscriptionType)
                .totalAmount(500L)
                .taxFreeAmount(50L)
                .quantity(1L)
                .build()));
    }
}