package com.flight.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentRequest {
    private String bookingId;
    private double amount;
    private String userId;
}
