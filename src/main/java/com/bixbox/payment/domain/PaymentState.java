package com.bixbox.payment.domain;

import javax.persistence.*;

@Entity
@Table(name="payment")
public class PaymentState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "state_id")
    private Long stateId;

    @Column(name="state_name", nullable = false)
    private String stateName;
}
