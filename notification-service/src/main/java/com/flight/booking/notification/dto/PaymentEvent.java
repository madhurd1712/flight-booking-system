package com.flight.booking.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PaymentEvent {
    private String eventId;
    private String paymentId;
    private long bookingId;
    private long userId;
    private double amount;
    private String status;
    private long timestamp;
}