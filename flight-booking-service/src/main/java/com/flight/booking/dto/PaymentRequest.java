package com.flight.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentRequest {
    private Long bookingId;
    private double amount;
    private Long userId;
}
