package com.pulsepay.payment_service.dto.response;

import java.util.Set;

import com.pulsepay.payment_service.enums.KycLevel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public class RegisterResponse {

    private Long id;
    private String name;
    private String email;
    private Set<String> roles;
    private KycLevel kycLevel;
  
}
