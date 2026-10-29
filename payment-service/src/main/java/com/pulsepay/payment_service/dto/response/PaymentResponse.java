package com.pulsepay.payment_service.dto.response;

import com.pulsepay.payment_service.enums.PaymentMethodType;
import com.pulsepay.payment_service.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PaymentResponse {

    private Long paymentId;
    private String merchantName;
    private BigDecimal amount;
    private String currency;
    private PaymentMethodType paymentMethodType;
    private PaymentStatus status;
    private String transactionRef;
    private String failureReason;
    private LocalDateTime createdAt;
}