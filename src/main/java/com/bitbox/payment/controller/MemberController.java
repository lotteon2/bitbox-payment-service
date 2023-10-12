package com.bitbox.payment.controller;

import com.bitbox.payment.domain.Payment;
import com.bitbox.payment.service.MemberService;
import com.bitbox.payment.service.response.PaymentPageCountResponse;
import com.bitbox.payment.service.response.SubscriptionResponse;
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

    @GetMapping("member/subscription")
    public ResponseEntity<SubscriptionResponse> memberSubscription() {
        return ResponseEntity.ok(memberService.getMemberSubscription(headerMemberId));
    }

    @GetMapping("member/{memberId}/subscription")
    public ResponseEntity<SubscriptionResponse> memberSubscription(@PathVariable String memberId) {
        return ResponseEntity.ok(memberService.getMemberSubscription(memberId));
    }

    @GetMapping("member/payments")
    public ResponseEntity<List<Payment>> memberPayments(Pageable pageable) {
        return ResponseEntity.ok(memberService.getPaymentsByMemberId(headerMemberId, pageable));
    }

    @GetMapping("member/payments/count")
    public ResponseEntity<PaymentPageCountResponse> memberPaymentCount(@RequestParam int size){
        return ResponseEntity.ok(memberService.getPaymentCountByMemberId(headerMemberId,size));
    }
}
