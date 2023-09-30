package com.bixbox.payment.util;

import com.bixbox.payment.dto.PaymentDto;
import com.bixbox.payment.exception.KakaoPayArgumentException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityValidationTest {
    @Test
    public void 크레딧_결제와_구독권_결제가_둘다_아닌경우_KakaoPayArgumentException_예외가_발생한다(){
        PaymentDto paymentDto = PaymentDto.builder().chargeCredit(null).subscriptionType(null).build();
        assertThrows(KakaoPayArgumentException.class, () -> EntityValidation.validPaymentDto(paymentDto));
    }
}