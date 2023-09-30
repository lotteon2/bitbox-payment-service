package com.bixbox.payment.service;

import com.bixbox.payment.domain.Payment;
import com.bixbox.payment.domain.Subscription;
import com.bixbox.payment.dto.KakaoPayDto;
import com.bixbox.payment.exception.SubscriptionExistException;
import com.bixbox.payment.repository.PaymentRepository;
import com.bixbox.payment.repository.SubscriptionRepository;
import io.github.bitbox.bitbox.enums.SubscriptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// 구독권 결제만 테스트 가능(크레딧의 경우 카카오 승인 API랑 카프카를 사용하므로)
@SpringBootTest
@Transactional
class PaymentServiceTest {
    @Autowired private PaymentService paymentService;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private SubscriptionRepository subscriptionRepository;
    private KakaoPayDto kakaoPayDto;
    private final String memberId="csh";
    @BeforeEach
    public void before(){
        kakaoPayDto = KakaoPayDto.builder()
                .cid("cid")
                .tid("tid")
                .itemName("itemName")
                .amount(1000L)
                .taxFreeAmount(1000L)
                .partnerOrderId("partnerOrderId")
                .partnerUserId(memberId)
                .pgToken("pgToken")
                .subscription(SubscriptionType.ONE_DAY)
                .credit(null)
                .build();
    }

    @Test
    public void 정상적인_경우_결제와_구독권_정보를_확인할수있다(){
        paymentService.createPayment(kakaoPayDto);
        List<Subscription> subscriptions = (List<Subscription>) subscriptionRepository.findAll();
        List<Payment> payments = (List<Payment>) paymentRepository.findAll();

        assertEquals(subscriptions.size(), 1);
        assertEquals(payments.size(), 1);
    }

    @Test
    public void 구독권_정보가_존재하는_경우_예외가_발생하며_결제_정보가_롤백이_제대로_되는가(){
        subscriptionRepository.save(Subscription.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .isValid(true)
                .memberId(memberId)
                .subscriptionType(SubscriptionType.ONE_DAY)
                .build());

        assertThrows(SubscriptionExistException.class, () -> paymentService.createPayment(kakaoPayDto));
    }

}