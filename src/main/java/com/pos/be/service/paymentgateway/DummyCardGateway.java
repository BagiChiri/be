package com.pos.be.service.paymentgateway;

import com.pos.be.paymetngateway.CardPaymentGateway;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class DummyCardGateway implements CardPaymentGateway {

    @Override
    public String processCardPayment(String cardTrackData, BigDecimal amount) {
        return "CARDREF-" + UUID.randomUUID().toString();
    }
}

