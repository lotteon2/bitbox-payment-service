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
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/{memberId}/subscription")
    public ResponseEntity<SubscriptionResponse> memberSubscription(@PathVariable String memberId) {
        return ResponseEntity.ok(memberService.getMemberSubscription(memberId));
    }

    @GetMapping("/{memberId}/payments")
    public List<Payment> memberPayments(@PathVariable String memberId,
                                            Pageable pageable) {
        return memberService.getPaymentsByMemberId(memberId, pageable);
    }

    @GetMapping("/{memberId}/payments/count")
    public ResponseEntity<PaymentPageCountResponse> memberPaymentCount(@PathVariable String memberId,
                                                                       @RequestParam int size){
        return ResponseEntity.ok(memberService.getPaymentCountByMemberId(memberId,size));
    }
}
