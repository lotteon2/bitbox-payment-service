package com.bixbox.payment.domain;

import com.bixbox.payment.dto.KakaoPayDto;
import io.github.bitbox.bitbox.enums.SubscriptionType;
import io.github.bitbox.bitbox.util.DateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="subscription")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long subscriptionId;

    @Column(name="member_id", nullable = false)
    private String memberId;

    @Column(name="start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name="end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name="is_valid", nullable = false)
    private boolean isValid;

    @Column(name="subscription_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;

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