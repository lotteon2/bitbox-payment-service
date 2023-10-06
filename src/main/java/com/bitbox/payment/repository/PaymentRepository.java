package com.bitbox.payment.repository;

import com.bitbox.payment.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    Page<Payment> findByMemberIdOrderByPaymentDateDesc(String memberId, Pageable pageable);
    int countByMemberId(String memberId);
}