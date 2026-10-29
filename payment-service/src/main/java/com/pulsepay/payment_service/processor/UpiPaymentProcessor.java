package com.pulsepay.payment_service.processor;

import java.math.BigDecimal;
import java.util.UUID;

import com.pulsepay.payment_service.entity.Payment;
import com.pulsepay.payment_service.enums.PaymentMethodType;

public class UpiPaymentProcessor  implements PaymentProcessor{

  private static final BigDecimal UPI_LIMIT = new BigDecimal("100000");
  private static final String UPI_ID_PATTERN = "^[a-zA-Z0-9.\\-_]{2,}@[a-zA-Z]{2,}$";


  @Override 
  public PaymentMethodType getType()
  {
    return PaymentMethodType.UPI;
  }

  public ProcessorResult process(Payment payment)
  {
    //get the upi id first to process 
    String UPI_ID = payment.getPaymentMethod().getUpiId();

    //check is upi id is valid or not 

    if(UPI_ID == null || !UPI_ID.matches(UPI_ID_PATTERN))
    {
      return ProcessorResult.failure("INVALID UPI ID");
    }

    //check does the payment is in limit or not

    if(payment.getAmount().compareTo(UPI_LIMIT) > 0)
    {
      return ProcessorResult.failure("UPI limit is ₹1,00,000 per payment");
    }

    //SEND TO BANK (SIMULATED : APPROVED) IN REAL PAYMENT GATEWAY


    //SUCCESS + REFERENCE

    String reference = "UPI-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();

    return ProcessorResult.success(reference);


  }

  
}
