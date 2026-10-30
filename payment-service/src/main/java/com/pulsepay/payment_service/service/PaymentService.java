package com.pulsepay.payment_service.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.pulsepay.payment_service.dto.request.CreatePaymentRequest;
import com.pulsepay.payment_service.dto.response.PaymentResponse;
import com.pulsepay.payment_service.entity.KycDetails;
import com.pulsepay.payment_service.entity.Merchant;
import com.pulsepay.payment_service.entity.Payment;
import com.pulsepay.payment_service.entity.PaymentMethod;
import com.pulsepay.payment_service.entity.PaymentStatusHistory;
import com.pulsepay.payment_service.entity.User;
import com.pulsepay.payment_service.enums.KycLevel;
import com.pulsepay.payment_service.enums.PaymentStatus;
import com.pulsepay.payment_service.exception.PaymentNotAllowedException;
import com.pulsepay.payment_service.exception.ResourceNotFoundException;
import com.pulsepay.payment_service.processor.PaymentProcessor;
import com.pulsepay.payment_service.processor.PaymentProcessorFactory;
import com.pulsepay.payment_service.processor.ProcessorResult;
import com.pulsepay.payment_service.repository.KycDetailsRepository;
import com.pulsepay.payment_service.repository.MerchantRepository;
import com.pulsepay.payment_service.repository.PaymentMethodRepository;
import com.pulsepay.payment_service.repository.PaymentRepository;
import com.pulsepay.payment_service.repository.PaymentStatusHistoryRepository;
import com.pulsepay.payment_service.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;   
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PaymentService {


    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentStatusHistoryRepository historyRepository;
    private final KycDetailsRepository kycDetailsRepository;
    private final PaymentProcessorFactory processorFactory;

    private static final BigDecimal BASIC_KYC_LIMIT = new BigDecimal("10000");
    private static final BigDecimal FULL_KYC_LIMIT  = new BigDecimal("200000");




    @Transactional
    public PaymentResponse createPayment(String email, CreatePaymentRequest request) {
        // we will fill this step by step

        // 1.  (WHO is paying → load the user ) now we have user email but email cant be stored in our paymnet table we need id of the user how do we find
        //okay now we have our method in userepository to find user by email we use that

        User user = userRepository.findByEmail(email)
                                  .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        //2.To whome we are paying

        
        Merchant merchant = merchantRepository.findById(request.getMerchantId())
                                              .orElseThrow(() -> new ResourceNotFoundException("Merchant not found: " + request.getMerchantId()));

        //3.now we found merchent but what if he is not active so we shouldnt allow to pay

        if (!merchant.isActive()) 
        {
        throw new PaymentNotAllowedException("Merchant is not active: " + merchant.getName());
        }

        // 4. WITH WHAT → load the payment method, only if it's this user's
        PaymentMethod paymentMethod = paymentMethodRepository.findByIdAndUserId(request.getPaymentMethodId(), user.getId())
                                                              .orElseThrow(() -> new ResourceNotFoundException("Payment method not found: " + request.getPaymentMethodId()));
        
        
        // 5. HOW MUCH is allowed → load the KYC details
        KycDetails kyc = kycDetailsRepository.findByUserId(user.getId())
                                              .orElseThrow(() -> new ResourceNotFoundException("KYC details not found for user: " + user.getId()));
      
        //6 .now we know what kyc level is basic or not and we have mentioned above basic means 10k and full means 2lakh now we need to tell that
        //limit is 10k and 2lakh how do we do that

        BigDecimal limit;

        if(kyc.getKycLevel() == KycLevel.BASIC)
        {

          limit = BASIC_KYC_LIMIT;

        }
        else
        {
          limit = FULL_KYC_LIMIT;
        }

        //7. check wether the amount sent by user is within the limit

        if(request.getAmount().compareTo(limit) > 0)
        {
          throw new PaymentNotAllowedException(
                    "Amount exceeds your " + kyc.getKycLevel() + " KYC limit of ₹" + limit);
        }

        // 9. all checks passed → create the payment as PENDING
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setMerchant(merchant);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(request.getAmount());
        payment.setIdempotencyKey(request.getIdempotencyKey());
        payment.setStatus(PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(payment);

        recordHistory(savedPayment, null, PaymentStatus.PENDING, "Payment created");

        //10.now the next big step if we need to pay the amount okay so for that we need to select the payment process
        //here as it is we know see paymentmethod.getype() here we get what is type now we need to call factory so it knows what processor to select to process
        //soo we call that 

        PaymentProcessor processor = processorFactory.getProcessor(paymentMethod.getType()); // what is the type upi or card goes here and the factory method is called and pass the type and there it selects the respective processor for that

        //11.now we know the processor now we need to process the payment soo we call the method by what object present in processor it implements taht method

        ProcessorResult result = processor.process(savedPayment);

        //12 . Read the result and update the payment -> history

        if(result.isSuccess()) // if true payment success now from pending -> successs
        {
          savedPayment.setStatus(PaymentStatus.SUCCESS);
          savedPayment.setTransactionRef(result.getTransactionRef());
          recordHistory(savedPayment, PaymentStatus.PENDING,PaymentStatus.SUCCESS, "Payment successful: " + result.getTransactionRef());
        }
        else
        {
           savedPayment.setStatus(PaymentStatus.FAILED);
            savedPayment.setFailureReason(result.getFailureReason());
            recordHistory(savedPayment, PaymentStatus.PENDING, PaymentStatus.FAILED,
                    result.getFailureReason());
        }

        Payment finalPayment = paymentRepository.save(savedPayment);

        // 13. build the response for the app
        return new PaymentResponse(
                finalPayment.getId(),
                merchant.getName(),
                finalPayment.getAmount(),
                finalPayment.getCurrency(),
                paymentMethod.getType(),
                finalPayment.getStatus(),
                finalPayment.getTransactionRef(),
                finalPayment.getFailureReason(),
                finalPayment.getCreatedAt()
        );
        
    }

        // status tarcking method it is called in service to check from to to 
        private void recordHistory(Payment payment, PaymentStatus from, PaymentStatus to, String reason) {
        PaymentStatusHistory history = new PaymentStatusHistory();
        history.setPayment(payment);
        history.setFromStatus(from);
        history.setToStatus(to);
        history.setReason(reason);
        historyRepository.save(history);
    }

  
}
