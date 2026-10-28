package com.pulsepay.payment_service.dto.response;

import com.pulsepay.payment_service.enums.KycLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String name;
    private String email;
    private Set<String> roles;
    private KycLevel kycLevel;
}