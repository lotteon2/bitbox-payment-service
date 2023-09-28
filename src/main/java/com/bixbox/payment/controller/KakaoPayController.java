package com.bixbox.payment.controller;

import com.bixbox.payment.exception.KakaoPayArgumentException;
import com.bixbox.payment.temp.PaymentDto;
import com.bixbox.payment.util.KakaoPayUtil;
import io.github.bitbox.bitbox.enums.SubscriptionType;
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
        validPaymentDto(paymentDto);

        return kakaoPayUtil.getKakaoTemplate("csh", paymentDto); // TODO partnerUserId는 헤더에서 가져옴
    }
    
    private void validPaymentDto(PaymentDto paymentDto){
        SubscriptionType subscriptionType = paymentDto.getSubscriptionType();
        Long chargeCredit = paymentDto.getChargeCredit();
        Long quantity = paymentDto.getQuantity();

        // 1. 크레딧 결제와 구독권 결제가 아닌 경우 혹은 크레딧과 구독권을 동시에 결제하는 경우
        if ((subscriptionType == null && chargeCredit == null) || (subscriptionType != null && chargeCredit != null)) {
            throw new KakaoPayArgumentException("크레딧과 구독권이 둘 다 비어있거나 동시에 결제하려고 할 수 없습니다");
        }

        // 2. subscriptionType이 null이 아니면서 quantity가 1이 아닌 경우
        if (subscriptionType != null && quantity != 1) {
            throw new KakaoPayArgumentException("구독권 결제의 경우 수량은 항상 1이여야 합니다");
        }
    }
}

// 구독권 결제(/payments/subscription) POST
/*
    {"partner_order_id":"주문코드",
      "item_name":"상품명",
       "quantity":"1",
        "totalAmount":"가격",
         "taxFreeAmount":"비과세가격",
         "subscriptionType":"0,1,2,null",
         "chargeCredit":1이상 or null}
 */
// 크레딧 결제(/peyments/credit) POST

// 내일 구독권 및 크레딧 결제 로직 완성 후 구독권, 결제내역 조회하는 메소드 구현하고 User쪽 보상패턴 및 Feign Client 완성
// 다음날은 테스트 및 코드 리뷰

// 구독권 조회(채팅 모듈에서 사용) -> 서킷브레이커 (/member/subscription) GET
// 결제내역 조회 -> /payments?page={page} GET

/* -> 다음주에 고려
    jwt 토큰자체(헤더) -> @RequestHeader("Authorization") String jwtToken
    페이로드(어디에 저장이 될까? 헤더 or 바디?)
    - 유저아이디(member_id)
    - 유저권한
 */