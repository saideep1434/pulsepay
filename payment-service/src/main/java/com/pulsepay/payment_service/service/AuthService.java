package com.pulsepay.payment_service.service;


import java.util.Set;

import com.pulsepay.payment_service.entity.KycDetails;
import com.pulsepay.payment_service.entity.Role;   // ✅ YOUR Role

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pulsepay.payment_service.dto.request.LoginRequest;
import com.pulsepay.payment_service.dto.request.RegisterRequest;
import com.pulsepay.payment_service.dto.response.LoginResponse;
import com.pulsepay.payment_service.dto.response.RegisterResponse;
import com.pulsepay.payment_service.entity.User;
import com.pulsepay.payment_service.enums.KycLevel;
import com.pulsepay.payment_service.enums.RoleName;
import com.pulsepay.payment_service.exception.EmailAlreadyExistsException;
import com.pulsepay.payment_service.exception.InvalidCredentialsException;
import com.pulsepay.payment_service.repository.KycDetailsRepository;
import com.pulsepay.payment_service.repository.RoleRepository;
import com.pulsepay.payment_service.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final KycDetailsRepository kycDetailsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

      // 1 . Check if the email already exists
      if(userRepository.existsByEmail(request.getEmail())) {
          throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
      }


      //2 . hash the password
      String hashedPassword = passwordEncoder.encode(request.getPassword());


      // 3. get the USER role from the database
      Role userRole = roleRepository.findByName(RoleName.USER).orElseThrow(()-> new IllegalStateException("USER role missing in roles table"));
      
      // 4. create the user and save
      User user = new User();
      user.setName(request.getName());
      user.setEmail(request.getEmail());
      user.setPasswordHash(hashedPassword);
      user.getRoles().add(userRole);
      User savedUser = userRepository.save(user);

      //5.create BASIC KYC
      KycDetails kycDetails = new KycDetails();
      kycDetails.setUser(savedUser);
      kycDetails.setKycLevel(KycLevel.BASIC);
      kycDetailsRepository.save(kycDetails);

      //6 . return the response
      RegisterResponse response = new RegisterResponse(
        savedUser.getId(),
        savedUser.getName(),
        savedUser.getEmail(),
        Set.of(userRole.getName().name()),
        kycDetails.getKycLevel()
      );

      return response;
      

}

    public LoginResponse login(LoginRequest request) 
    {
      
      // 1. find user by email

      User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
      
      
      // 2. check if password matches

      if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
          throw new InvalidCredentialsException("Invalid email or password");
      }

      // 3. generate JWT token
      String token = jwtService.generateToken(user);

      return new LoginResponse(token, "Bearer", jwtService.getExpirationSeconds());

    
    
    }

}
