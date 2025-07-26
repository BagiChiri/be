package com.pos.be.paymetngateway;

import com.pos.be.exception.PaymentProcessingException;

import java.math.BigDecimal;

public interface CardPaymentGateway {
    String processCardPayment(String cardTrackData, BigDecimal amount) throws PaymentProcessingException;
}


