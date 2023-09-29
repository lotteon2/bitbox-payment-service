package com.bixbox.payment.controller;

import com.bixbox.payment.dto.PaymentDto;
import com.bixbox.payment.util.EntityValidation;
import com.bixbox.payment.util.KakaoPayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/kakao-pay")
@RequiredArgsConstructor
public class KakaoPayController {
    private final KakaoPayUtil kakaoPayUtil;
    @PostMapping("/payment-request")
    public ResponseEntity<String> paymentRequest(@RequestBody @Valid PaymentDto paymentDto){
        EntityValidation.validPaymentDto(paymentDto);

        return kakaoPayUtil.callKakaoReadyApi("csh", paymentDto); // TODO partnerUserId는 헤더에서 가져옴
    }

}