package com.flight.booking.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {
    private String eventId;
    private String bookingId;
    private String userId;
    private String flightId;
    private int seats;
    private String status;
    private long timestamp;
    private double amount;
}