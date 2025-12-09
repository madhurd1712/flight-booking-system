package com.flight.booking.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PaymentEvent {
    private String eventId;
    private String paymentId;
    private String bookingId;
    private String userId;
    private double amount;
    private String status;
    private long timestamp;
}