package com.bixbox.payment.domain;

import io.github.bitbox.bitbox.enums.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name="subscriptionType", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private SubscriptionType subscriptionType;
}