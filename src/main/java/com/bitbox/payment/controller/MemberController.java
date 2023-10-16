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

    @GetMapping("member/subscription")
    public ResponseEntity<SubscriptionResponse> memberSubscription(@RequestHeader String memberId) {
        return ResponseEntity.ok(memberService.getMemberSubscription(memberId));
    }

    @GetMapping("member/{memberId}/subscription")
    public ResponseEntity<SubscriptionResponse> targetMemberSubscription(@PathVariable String memberId) {
        return ResponseEntity.ok(memberService.getMemberSubscription(memberId));
    }

    @GetMapping("member/payments")
    public ResponseEntity<List<Payment>> memberPayments(@RequestHeader String memberId, Pageable pageable) {
        return ResponseEntity.ok(memberService.getPaymentsByMemberId(memberId, pageable));
    }

    @GetMapping("member/payments/count")
    public ResponseEntity<PaymentPageCountResponse> memberPaymentCount(@RequestHeader String memberId, @RequestParam int size){
        return ResponseEntity.ok(memberService.getPaymentCountByMemberId(memberId,size));
    }
}
