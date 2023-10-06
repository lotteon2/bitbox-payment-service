package com.bitbox.payment.repository;

import com.bitbox.payment.domain.Subscription;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {
    Optional<Subscription> findByMemberIdAndIsValidTrue(String memberId);
}