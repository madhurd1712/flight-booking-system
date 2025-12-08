package com.flight.booking.search.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private double price;
    private int availableSeats;
}
