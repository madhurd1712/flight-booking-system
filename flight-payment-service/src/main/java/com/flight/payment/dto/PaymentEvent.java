package com.flight.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {
    private String eventId;
    private String paymentId;
    private long bookingId;
    private long userId;
    private double amount;
    private String status;
    private long timestamp;
}