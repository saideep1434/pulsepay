package com.pulsepay.payment_service.repository;

import com.pulsepay.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    Optional<Payment> findByIdAndUserId(Long id, Long userId);

    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);
}