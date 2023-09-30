package com.bixbox.payment.service.response;

import com.bixbox.payment.domain.Subscription;
import io.github.bitbox.bitbox.enums.SubscriptionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
public class SubscriptionResponse {
    private boolean isValid;
    private SubscriptionType subscriptionType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder
    private SubscriptionResponse(boolean isValid, SubscriptionType subscriptionType, LocalDateTime startDate, LocalDateTime endDate) {
        this.isValid = isValid;
        this.subscriptionType = subscriptionType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static SubscriptionResponse getSubscriptionResponse(Optional<Subscription> subscription) {
        return subscription.map(sub -> SubscriptionResponse.builder()
                        .isValid(sub.isValid())
                        .subscriptionType(sub.getSubscriptionType())
                        .startDate(sub.getStartDate())
                        .endDate(sub.getEndDate())
                        .build())
                .orElseGet(() -> SubscriptionResponse.builder()
                        .isValid(false)
                        .subscriptionType(null)
                        .startDate(null)
                        .endDate(null)
                        .build());
    }

}
