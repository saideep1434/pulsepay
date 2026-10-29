package com.pulsepay.payment_service.processor;

import com.pulsepay.payment_service.entity.Payment;
import com.pulsepay.payment_service.enums.PaymentMethodType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CardPaymentProcessor implements PaymentProcessor {

    private static final BigDecimal CARD_LIMIT = new BigDecimal("200000");

    @Override
    public PaymentMethodType getType() {
        return PaymentMethodType.CARD;
    }

    @Override
    public ProcessorResult process(Payment payment) {

        String last4 = payment.getPaymentMethod().getCardLast4();

        // 1. a card must be saved
        if (last4 == null || !last4.matches("\\d{4}")) {
            return ProcessorResult.failure("Card details missing");
        }

        // 2. the card limit
        if (payment.getAmount().compareTo(CARD_LIMIT) > 0) {
            return ProcessorResult.failure("Card limit is ₹2,00,000 per payment");
        }

        // 3. the bank decline (simulated test card)
        if (last4.equals("0000")) {
            return ProcessorResult.failure("Card declined by bank");
        }

        // 4. approved → reference
        String reference = "CARD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return ProcessorResult.success(reference);
    }
}