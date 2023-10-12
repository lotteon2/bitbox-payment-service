package com.bitbox.payment.controller;

import com.bitbox.payment.dto.KakaoPayDto;
import com.bitbox.payment.service.PaymentService;
import com.bitbox.payment.util.KakaoPayUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private final KakaoPayUtil kakaoPayUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @RequestMapping
    public ResponseEntity<Void> kakaoSuccess(@RequestParam("partnerOrderId") String partnerOrderId,
                                       @RequestParam("pg_token") String pgToken){
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        KakaoPayDto kakaoPayDto = kakaoPayUtil.getKakaoPayDto(vop.get(partnerOrderId), pgToken);

        paymentService.createPayment(kakaoPayDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fail")
    public ResponseEntity<Void> kakaoFail() {
        return ResponseEntity.internalServerError().build();
    }
}

/* -> 다음주에 고려
    jwt 토큰자체(헤더) -> @RequestHeader("Authorization") String jwtToken
    페이로드(어디에 저장이 될까? 헤더 or 바디?)
    - 유저아이디(member_id)
    - 유저권한
 */