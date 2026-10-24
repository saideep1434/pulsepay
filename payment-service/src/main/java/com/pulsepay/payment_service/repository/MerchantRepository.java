package com.pulsepay.payment_service.repository;

import com.pulsepay.payment_service.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    List<Merchant> findByActiveTrue();
}
