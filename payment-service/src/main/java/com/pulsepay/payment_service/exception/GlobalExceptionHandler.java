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

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex)
  {
    ErrorResponse error = new ErrorResponse(
            401,
            "INVALID_CREDENTIALS",
            ex.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex)
  {
    ErrorResponse error = new ErrorResponse(
            404,
            "RESOURCE_NOT_FOUND",
            ex.getMessage(),
            LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(UnsupportedPaymentMethodException.class)
   public ResponseEntity<ErrorResponse> handleUnsupportedMethod(UnsupportedPaymentMethodException ex)
  {
    ErrorResponse error = new ErrorResponse
    (400, 
      "UNSUPPORTED_PAYMENT_METHOD", 
      ex.getMessage(), 
      LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(PaymentNotAllowedException.class)
   public ResponseEntity<ErrorResponse> handlePaymentNotAllowed(PaymentNotAllowedException  ex)
  {
    ErrorResponse error = new ErrorResponse
    (422, 
      "UNPROCESSABLE_CONTENT", 
      ex.getMessage(), 
      LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(error);
  }
  
}
