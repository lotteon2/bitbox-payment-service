package com.bitbox.payment.service;

import com.bitbox.payment.domain.Payment;
import com.bitbox.payment.exception.NotFoundException;
import com.bitbox.payment.repository.PaymentRepository;
import com.bitbox.payment.repository.SubscriptionRepository;
import com.bitbox.payment.service.response.PaymentPageCountResponse;
import com.bitbox.payment.service.response.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;

    public SubscriptionResponse getMemberSubscription(String memberId){
        return SubscriptionResponse.getSubscriptionResponse(subscriptionRepository.findByMemberIdAndIsValidTrue(memberId));
    }

    public List<Payment> getPaymentsByMemberId(String memberId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByMemberIdOrderByPaymentDateDesc(memberId, pageable);
        if(payments.isEmpty()){
            throw new NotFoundException("잘못된 페이지 요청입니다.");
        }
        return payments.getContent();
    }

    public PaymentPageCountResponse getPaymentCountByMemberId(String memberId, int pageSize){
        return PaymentPageCountResponse.getPaymentPageCountResponse(paymentRepository.countByMemberId(memberId),pageSize);
    }
}