package com.flight.booking.inventory.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class InventoryService {

    // simple in-memory inventory store for demo: flightId -> available seats
    private final Map<String, Integer> inventory = new ConcurrentHashMap<>();

    public InventoryService() {
        // seed with some flights for testing (optional)
        inventory.put("FLIGHT-100", 10);
        inventory.put("FLIGHT-200", 5);
    }

    /**
     * Attempts to lock seats. Returns true if success; false otherwise.
     */
    public synchronized boolean lockSeats(String flightId, int seats) {
        Integer available = inventory.getOrDefault(flightId, 0);
        if (available >= seats) {
            inventory.put(flightId, available - seats);
            return true;
        }
        return false;
    }

    public synchronized void releaseSeats(String flightId, int seats) {
        inventory.put(flightId, inventory.getOrDefault(flightId, 0) + seats);
    }
}
