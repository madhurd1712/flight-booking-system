package com.flight.search.controller;

import com.flight.search.entity.Flight;
import com.flight.search.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class SearchController {

    private final FlightRepository repository;

    @GetMapping("/search")
    public List<Flight> search(@RequestParam String origin, @RequestParam String destination, @RequestParam String date) {
        LocalDateTime start = LocalDateTime.parse(date + "T00:00:00");
        LocalDateTime end = LocalDateTime.parse(date + "T23:59:59");
        return repository.findByOriginAndDestinationAndDepartureTimeBetween(origin, destination, start, end);
    }

    @GetMapping("/{id}/availability")
    public boolean check(@PathVariable Long id, @RequestParam int seats) {
        return repository.findById(id).map(f -> f.getAvailableSeats() >= seats).orElse(false);
    }

    @PostMapping("/seed")
    public Flight seed(@RequestBody Flight flight) {
        return repository.save(flight);
    }
}
