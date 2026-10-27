package com.pulsepay.payment_service.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pulsepay.payment_service.dto.response.ErrorResponse;

@RestControllerAdvice 
public class GlobalExceptionHandler {

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex)
  {
    ErrorResponse error = new ErrorResponse(
      409,"EMAIL_ALREADY_EXISTS",ex.getMessage(),LocalDateTime.now());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);

  }
  
}
