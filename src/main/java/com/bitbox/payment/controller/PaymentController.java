package com.bitbox.payment.controller;

import com.bitbox.payment.dto.KakaoPayDto;
import com.bitbox.payment.service.PaymentService;
import com.bitbox.payment.util.KakaoPayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final KakaoPayUtil kakaoPayUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final String SUCCESS_URL = "success";
    private final String CANCEL_URL = ""; // 진짜 문자열이 없음
    private final String FAIL_URL = "fail";

    @RequestMapping
    public String kakaoSuccess(@RequestParam("partnerOrderId") String partnerOrderId,
                                       @RequestParam("pg_token") String pgToken){
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        KakaoPayDto kakaoPayDto = kakaoPayUtil.getKakaoPayDto(vop.get(partnerOrderId), pgToken);

        paymentService.createPayment(kakaoPayDto);
        return kakaoPayUtil.generatePageRedirectionCode(SUCCESS_URL);
    }

    @GetMapping("/fail")
    public String kakaoFail() {
        return kakaoPayUtil.generatePageRedirectionCode(FAIL_URL);
    }

    @GetMapping("/cancel")
    public String kakaoCancel(){
        return kakaoPayUtil.generatePageRedirectionCode(CANCEL_URL);
    }
}