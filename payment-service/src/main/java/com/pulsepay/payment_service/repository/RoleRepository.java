package com.pulsepay.payment_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulsepay.payment_service.entity.Role;
import com.pulsepay.payment_service.enums.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(RoleName name);
  
}
