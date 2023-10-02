package com.bixbox.payment.controller;

import com.bixbox.payment.domain.Payment;
import com.bixbox.payment.service.MemberService;
import com.bixbox.payment.service.response.PaymentPageCountResponse;
import com.bixbox.payment.service.response.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class MemberController {
    private final MemberService memberService;
    private String headerMemberId = "csh";

    @GetMapping("/subscription")
    public ResponseEntity<SubscriptionResponse> memberSubscription() {
        return ResponseEntity.ok(memberService.getMemberSubscription(headerMemberId));
    }

    @GetMapping("/payments")
    public List<Payment> memberPayments(Pageable pageable) {
        return memberService.getPaymentsByMemberId(headerMemberId, pageable);
    }

    @GetMapping("/payments/count")
    public ResponseEntity<PaymentPageCountResponse> memberPaymentCount(@RequestParam int size){
        return ResponseEntity.ok(memberService.getPaymentCountByMemberId(headerMemberId,size));
    }
}
