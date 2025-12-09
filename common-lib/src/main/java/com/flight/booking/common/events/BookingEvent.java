package com.flight.booking.common.events;

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
    private String status; // CREATED | CONFIRMED | CANCELLED
    private long timestamp;
    private double amount;
}