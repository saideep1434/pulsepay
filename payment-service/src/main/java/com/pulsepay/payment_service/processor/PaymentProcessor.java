package com.pulsepay.payment_service.processor;

import com.pulsepay.payment_service.entity.Payment;
import com.pulsepay.payment_service.enums.PaymentMethodType;

public interface PaymentProcessor {

        PaymentMethodType getType();

        ProcessorResult process(Payment payment);
  
}
