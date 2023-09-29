package com.bixbox.payment.controller;

import com.bixbox.payment.dto.KakaoPayDto;
import com.bixbox.payment.service.PaymentService;
import com.bixbox.payment.util.KakaoPayUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private final KakaoPayUtil kakaoPayUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @RequestMapping
    public String kakaoSuccess(@RequestParam("partnerOrderId") String partnerOrderId,
                               @RequestParam("pg_token") String pgToken){
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        KakaoPayDto kakaoPayDto = kakaoPayUtil.getKakaoPayDto(vop.get(partnerOrderId), pgToken);

        paymentService.createPayment(kakaoPayDto);
        return generatePageCloseCodeWithAlert("카카오페이 결제가 제대로 수행되었습니다.");
    }

    @GetMapping("/fail")
    public String kakaoFail() {
        return generatePageCloseCodeWithAlert("카카오페이 결제가 정상적으로 종료되지 않았습니다.");
    }

    private String generatePageCloseCodeWithAlert(String alertMessage) {
        String htmlCode = "<!DOCTYPE html><html><head></head><body>";
        htmlCode += "<script>";
        htmlCode += "window.onload = function() {";
        htmlCode += "  alert('" + alertMessage + "');";
        htmlCode += "  window.close();";
        htmlCode += "};";
        htmlCode += "</script>";
        htmlCode += "</body></html>";

        return htmlCode;
    }
}

// 구독권 조회(채팅 모듈에서 사용) -> 서킷브레이커 (/member/subscription) GET
// 결제내역 조회 -> /payments?page={page} GET
// 테스트코드 작성 및 코드리뷰?

/* -> 다음주에 고려
    jwt 토큰자체(헤더) -> @RequestHeader("Authorization") String jwtToken
    페이로드(어디에 저장이 될까? 헤더 or 바디?)
    - 유저아이디(member_id)
    - 유저권한
 */