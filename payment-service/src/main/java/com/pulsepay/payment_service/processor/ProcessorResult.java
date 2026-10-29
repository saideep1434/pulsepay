package com.pulsepay.payment_service.processor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public class ProcessorResult {

      private boolean Success;
      private String transactionRef;
      private String failureReason;

      public static ProcessorResult success(String transctionRef)
      {
        return new ProcessorResult(true,transctionRef,null);
      }

      public static ProcessorResult failure(String failurereason)
      {
        return new ProcessorResult(false, null, failurereason);
      }
  
}
