package com.bixbox.payment.util;

import com.bixbox.payment.dto.PaymentDto;
import com.bixbox.payment.exception.KakaoPayArgumentException;
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
        PaymentDto paymentDto = PaymentDto.builder().chargeCredit(10L).subscriptionType(null).build();
        EntityValidation.validPaymentDto(paymentDto);
    }

    @Test
    public void 구독권만존재하고_크레딧정보가_존재하지않는데_수량이1이아닌경우_KakaoPayArgumentException_예외가_발생한다(){
        PaymentDto paymentDto = PaymentDto.builder().chargeCredit(null).subscriptionType(SubscriptionType.ONE_DAY).quantity(2L).build();
        assertThrows(KakaoPayArgumentException.class, () -> EntityValidation.validPaymentDto(paymentDto));
    }

    @Test
    public void 구독권만존재하고_크레딧정보가_존재하지않는데_수량이1이면_정상처리된다(){
        PaymentDto paymentDto = PaymentDto.builder().chargeCredit(null).subscriptionType(SubscriptionType.ONE_DAY).quantity(1L).build();
        EntityValidation.validPaymentDto(paymentDto);
    }
}