package com.bixbox.payment.controller;

import com.bixbox.payment.service.PaymentService;
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
    private final RedisTemplate<String, String> redisTemplate;

    @RequestMapping
    public String kakaoSuccess(@RequestParam("partnerOrderId") String partnerOrderId,
                               @RequestParam("pg_token") String pgToken){
        /*  cid : 가능
            tid : 준비API응답,
            partner_order_id: 파라미터,
            partner_user_id:  헤더에 있음(가능할듯),
            pg_token: 가능
         */

        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        log.info("value = {}",vop.get(partnerOrderId));
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