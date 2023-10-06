package com.bitbox.payment.service;

import com.bitbox.payment.domain.Payment;
import com.bitbox.payment.domain.Subscription;
import com.bitbox.payment.dto.KakaoPayDto;
import com.bitbox.payment.exception.KakaoPayFailException;
import com.bitbox.payment.exception.SubscriptionExistException;
import com.bitbox.payment.repository.PaymentRepository;
import com.bitbox.payment.repository.SubscriptionRepository;
import com.bitbox.payment.util.KakaoPayUtil;
import io.github.bitbox.bitbox.dto.MemberPaymentDto;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final KafkaTemplate<String, MemberPaymentDto> kafkaTemplate;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final KakaoPayUtil kakaoPayUtil;
    @Value("${creditTopic}")
    private String creditTopic;

    @Transactional
    public void createPayment(KakaoPayDto kakaoPayDto) {
        // 결제 테이블(payment)에 결제 정보를 insert 한다
        paymentRepository.save(Payment.createKakaoPayDtoToPayment(kakaoPayDto));

        if (kakaoPayDto.getCredit() == null) { // 구독권 결제인 경우 구독권 관련 테이블에 insert 후 early return
            subscriptionRepository.findByMemberIdAndIsValidTrue(kakaoPayDto.getPartnerUserId()).ifPresent(subscription -> {
                throw new SubscriptionExistException("구독권 정보가 존재합니다");
            });
            subscriptionRepository.save(Subscription.createKakaoPayDtoToSubscription(kakaoPayDto));
            return;
        }

        if (kakaoPayUtil.callKakaoApproveApi(kakaoPayDto) != HttpStatus.SC_OK) {
            throw new KakaoPayFailException("카카오 페이 결제 실패");
        }

        kafkaTemplate.send(creditTopic, MemberPaymentDto.builder()
                        .memberId(kakaoPayDto.getPartnerUserId())
                        .memberCredit(kakaoPayDto.getCredit())
                        .tid(kakaoPayDto.getTid())
                        .cancelAmount(kakaoPayDto.getAmount())
                        .cancelTaxFreeAmount(kakaoPayDto.getTaxFreeAmount())
                        .build());
    }
}