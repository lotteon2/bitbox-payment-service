package com.bitbox.payment.controller;

import com.bitbox.payment.dto.PaymentDto;
import com.bitbox.payment.util.EntityValidation;
import com.bitbox.payment.util.KakaoPayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/kakao-pay")
@RequiredArgsConstructor
public class KakaoPayController {
    private final KakaoPayUtil kakaoPayUtil;
    @PostMapping("/payment-request")
    public ResponseEntity<String> paymentRequest(@RequestHeader String memberId, @RequestBody @Valid PaymentDto paymentDto){
        EntityValidation.validPaymentDto(paymentDto);
        paymentDto.setPartnerUserId(memberId);

        return kakaoPayUtil.callKakaoReadyApi(paymentDto);
    }
}