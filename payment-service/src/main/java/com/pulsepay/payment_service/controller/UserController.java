package com.pulsepay.payment_service.controller;

import com.pulsepay.payment_service.dto.response.UserProfileResponse;
import com.pulsepay.payment_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(Authentication authentication) // authentication comes from our jwtAuthentication filter there we get apyload and we store that in authentication
    {
        String email = authentication.getName();
        UserProfileResponse response = userService.getMyProfile(email);
        return ResponseEntity.ok(response);
    }
}
