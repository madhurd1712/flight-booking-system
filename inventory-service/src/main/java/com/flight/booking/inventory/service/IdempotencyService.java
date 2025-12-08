package com.flight.booking.inventory.service;

import com.flight.booking.inventory.entity.ProcessedEvent;
import com.flight.booking.inventory.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final ProcessedEventRepository repo;

    public boolean isProcessed(String eventId) {
        return repo.existsById(eventId);
    }

    public void markProcessed(String eventId) {
        ProcessedEvent evt = new ProcessedEvent();
        evt.setEventId(eventId);
        repo.save(evt);
    }
}