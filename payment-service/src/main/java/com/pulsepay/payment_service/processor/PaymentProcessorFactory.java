package com.pulsepay.payment_service.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.pulsepay.payment_service.enums.PaymentMethodType;
import com.pulsepay.payment_service.exception.UnsupportedPaymentMethodException;

@Component 
public class PaymentProcessorFactory {

  //1 . create a map to store the paymenmethod and its processor 

  Map<PaymentMethodType,PaymentProcessor> processors = new HashMap<>();

  //2. now we have list of paymentprocessors already in spring now spring can give it for us 
  //generally in list we have like {upi processor , card processor}

  public PaymentProcessorFactory(List<PaymentProcessor>ProcessorList) // in processorlist we have our processors
  {
    //now take each payment type and its processor and put it in our map
    for(PaymentProcessor processor : ProcessorList)
    {
      processors.put(processor.getType(),processor); // here we get suppose say upi {upi,upiprocessor}
    }
  }

  //3.now if we know the paymentmethod type we can know the processor because in map we have values 
  public PaymentProcessor getProcessor(PaymentMethodType type)
  {
    PaymentProcessor processor = processors.get(type);

    if(processor == null)
    {
      throw new UnsupportedPaymentMethodException("Unsupported payment method: " + type);
    }

    return processor;
  }
  


  
}
