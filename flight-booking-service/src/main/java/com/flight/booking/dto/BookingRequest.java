package com.flight.booking.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private String flightId;
    private String userId;
    private int seats;
    private double amount;
}
