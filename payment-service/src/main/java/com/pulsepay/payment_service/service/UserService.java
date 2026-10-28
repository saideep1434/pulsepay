package com.pulsepay.payment_service.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.pulsepay.payment_service.dto.response.UserProfileResponse;
import com.pulsepay.payment_service.entity.KycDetails;
import com.pulsepay.payment_service.entity.Role;
import com.pulsepay.payment_service.entity.User;
import com.pulsepay.payment_service.exception.ResourceNotFoundException;
import com.pulsepay.payment_service.repository.KycDetailsRepository;
import com.pulsepay.payment_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class UserService {

        private final UserRepository userRepository;
        private final KycDetailsRepository kycDetailsRepository;
        

      // 1 . First we have email by using that we need to feth the user details
      public UserProfileResponse getMyProfile(String email)
      {

      User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email : " + email)); // here we got all the deatils for the email id if not found then we will throw the exception(not only email we get all the details for the user)
      
      //2 . Now we have user details we need to fetch the kyc details for the user

      KycDetails kycDetails = kycDetailsRepository.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("KYC details not found for user with email : " + email)); // here we got all the deatils for the user if not found then we will throw the exception(not only email we get all the details for the user)
  
      //3. Now we also need roles but we need to respond in text 

      Set<String> roles = new HashSet<>();
      for(Role role : user.getRoles())
      {
        roles.add(role.getName().name());
      }

      //4. Now we need to return the response in the form of UserProfileResponse

      UserProfileResponse userProfileResponse = new UserProfileResponse(
        user.getId(),
        user.getName(),
        user.getEmail(),
        roles,
        kycDetails.getKycLevel()
      );

      return userProfileResponse;
    }
  
}
