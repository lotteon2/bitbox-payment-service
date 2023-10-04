package com.bixbox.payment.service;

import com.bixbox.payment.dto.KakaoPayCancelDto;
import com.bixbox.payment.util.KakaoPayUtil;
import io.github.bitbox.bitbox.dto.MemberPaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelService {
    private final KakaoPayUtil kakaoPayUtil;

    @KafkaListener(topics = "${cancelTopic}")
    public void cancelKakaoPay(MemberPaymentDto memberPaymentDto) {
        kakaoPayUtil.callKakaoCancelApi(KakaoPayCancelDto.MemberPaymentDtoToKakaoPayCancelDto(memberPaymentDto));
    }
}
