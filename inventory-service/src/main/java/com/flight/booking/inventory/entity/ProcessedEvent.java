package com.flight.booking.inventory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ProcessedEvent {
    @Id
    private String eventId;
}