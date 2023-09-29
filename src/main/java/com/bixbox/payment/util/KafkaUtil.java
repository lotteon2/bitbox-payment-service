package com.bixbox.payment.util;

import com.bixbox.payment.dto.KakaoPayDto;
import io.github.bitbox.bitbox.dto.MemberPaymentDto;
import io.github.bitbox.bitbox.util.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaUtil {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${creditTopic}")
    private String creditTopic;

    public void callKafkaWithKakaoPayDto(KakaoPayDto kakaoPayDto, Long credit){
        KafkaProducer.send(kafkaTemplate, creditTopic, MemberPaymentDto.builder()
                .memberId(kakaoPayDto.getPartnerUserId())
                .memberCredit(credit)
                .tid(kakaoPayDto.getTid())
                .cancelAmount(kakaoPayDto.getAmount())
                .cancelTaxFreeAmount(kakaoPayDto.getTaxFreeAmount())
                .build());
    }
}
