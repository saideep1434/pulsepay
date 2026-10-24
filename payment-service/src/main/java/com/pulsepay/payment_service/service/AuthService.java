package com.pulsepay.payment_service.service;


import org.springframework.stereotype.Service;

import com.pulsepay.payment_service.dto.request.RegisterRequest;
import com.pulsepay.payment_service.dto.response.RegisterResponse;
import com.pulsepay.payment_service.exception.EmailAlreadyExistsException;
import com.pulsepay.payment_service.repository.KycDetailsRepository;
import com.pulsepay.payment_service.repository.RoleRepository;
import com.pulsepay.payment_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final KycDetailsRepository kycDetailsRepository;

    public RegisterResponse register(RegisterRequest request) {

      // 1 . Check if the email already exists
      if(userRepository.existsByEmail(request.getEmail())) {
          throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
      }

      

      return null;





}

}
