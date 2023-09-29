package com.bixbox.payment.service;

import com.bixbox.payment.dto.KakaoPayCancelDto;
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
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }

        kakaoPayUtil.callKakaoCancelApi(KakaoPayCancelDto.MemberPaymentDtoToKakaoPayCancelDto(memberPaymentDto));
    }
}
