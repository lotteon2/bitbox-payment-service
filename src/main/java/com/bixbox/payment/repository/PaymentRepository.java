package com.bixbox.payment.repository;

import com.bixbox.payment.domain.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
}