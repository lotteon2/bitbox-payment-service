package com.bixbox.payment.domain;

import com.bixbox.payment.dto.KakaoPayDto;
import com.bixbox.payment.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name="payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name="member_id", nullable = false)
    private String memberId;

    @Column(name="payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name="payment_amount", nullable = false)
    private Long paymentAmount;

    @Column(name="tax_free_amount", nullable = false)
    private Long taxFreeAmount;

    @Column(name="product_name", nullable = false)
    private String productName;

    @Column(name="payment_serial", nullable = false)
    private String paymentSerial; // tid

    @Column(name="payment_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

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
}