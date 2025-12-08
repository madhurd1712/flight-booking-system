package com.flight.payment.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String bookingId;
    private double amount;
    private String userId;
}
