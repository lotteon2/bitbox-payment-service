package com.bixbox.payment.service;

import com.bixbox.payment.dto.KakaoPayCancelDto;
import com.bixbox.payment.exception.UrgentMailException;
import com.bixbox.payment.util.KakaoPayUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bitbox.bitbox.dto.MemberPaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelService {
    private final KakaoPayUtil kakaoPayUtil;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "${cancelTopic}")
    public void cancelKakaoPay(String kafkaMessage) {
        MemberPaymentDto memberPaymentDto;
        try {
            memberPaymentDto = mapper.readValue(kafkaMessage, new TypeReference<>() {});
        } catch (JsonProcessingException ex) { // 보내주는 쪽에서 잘못된 타입으로 보내주는 케이스
            throw new UrgentMailException(ex);
        }

        kakaoPayUtil.callKakaoCancelApi(KakaoPayCancelDto.MemberPaymentDtoToKakaoPayCancelDto(memberPaymentDto));
    }
}
