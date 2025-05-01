package com.pos.be.paymetngateway;

import com.pos.be.exception.PaymentProcessingException;

import java.math.BigDecimal;

// com.pos.be.payment.CardPaymentGateway.java
// CardPaymentGateway Interface
public interface CardPaymentGateway {
    String processCardPayment(String cardTrackData, BigDecimal amount) throws PaymentProcessingException;
}


