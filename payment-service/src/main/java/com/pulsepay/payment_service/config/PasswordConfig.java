package com.pulsepay.payment_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {


  @Bean
  public PasswordEncoder paswordEncoder() {
    return new BCryptPasswordEncoder();
  }




  
}
