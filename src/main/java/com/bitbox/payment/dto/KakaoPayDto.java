package com.bitbox.payment.dto;

import com.bitbox.payment.domain.Payment;
import com.bitbox.payment.domain.Subscription;
import io.github.bitbox.bitbox.enums.PaymentType;
import io.github.bitbox.bitbox.enums.SubscriptionType;
import io.github.bitbox.bitbox.util.DateTimeUtil;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class KakaoPayDto {
    private String cid;
    private String tid;
    private String itemName;
    private Long amount;
    private Long taxFreeAmount;
    private String partnerOrderId;
    private String partnerUserId;
    private String pgToken;
    private SubscriptionType subscription;
    private Long credit;

    public void setPgToken(String pgToken) {
        this.pgToken = pgToken;
    }

    public static Payment createKakaoPayDtoToPayment(KakaoPayDto kakaoPayDto){
        return Payment.builder()
                .memberId(kakaoPayDto.getPartnerUserId())
                .paymentDate(LocalDateTime.now())
                .paymentAmount(kakaoPayDto.getAmount())
                .taxFreeAmount(kakaoPayDto.getTaxFreeAmount())
                .productName(kakaoPayDto.getItemName())
                .paymentSerial(kakaoPayDto.getTid())
                .paymentType(PaymentType.KAKAOPAY).build();
    }

    public static Subscription createKakaoPayDtoToSubscription(KakaoPayDto kakaoPayDto){
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime endTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        switch (kakaoPayDto.getSubscription()) {
            case ONE_DAY:
                endTime = currentTime.plusDays(1);
                break;
            case THREE_DAYS:
                endTime = currentTime.plusDays(3);
                break;
            case SEVEN_DAYS:
                endTime = currentTime.plusDays(7);
                break;
            default:
                throw new RuntimeException("존재하지 않는 구독권 타입입니다.");
        }

        return Subscription.builder()
                .memberId(kakaoPayDto.getPartnerUserId())
                .startDate(DateTimeUtil.convertTimeFormat(currentTime.format(formatter)))
                .endDate(DateTimeUtil.convertTimeFormat(endTime.format(formatter)))
                .isValid(true)
                .subscriptionType(kakaoPayDto.getSubscription())
                .build();
    }
}