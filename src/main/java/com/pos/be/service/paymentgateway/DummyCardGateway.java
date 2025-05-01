package com.pos.be.service.paymentgateway;

import com.pos.be.paymetngateway.CardPaymentGateway;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

// com.pos.be.payment.impl.DummyCardGateway.java
// DummyCardGateway Implementation
@Service
public class DummyCardGateway implements CardPaymentGateway {

    @Override
    public String processCardPayment(String cardTrackData, BigDecimal amount) {
        // In real life, you would call the actual payment gateway here.
        // This dummy implementation just generates a random reference number.
        return "CARDREF-" + UUID.randomUUID().toString();
    }
}

