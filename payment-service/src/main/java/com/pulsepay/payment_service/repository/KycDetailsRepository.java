package com.pulsepay.payment_service.repository;

import com.pulsepay.payment_service.entity.KycDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycDetailsRepository extends JpaRepository<KycDetails, Long> {

    Optional<KycDetails> findByUserId(Long userId);
}
