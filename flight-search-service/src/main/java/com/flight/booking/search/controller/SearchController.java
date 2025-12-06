package com.flight.booking.search.controller;

import com.flight.booking.search.entity.Flight;
import com.flight.booking.search.repository.FlightRepository;
import com.flight.booking.search.service.FlightSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class SearchController {

    private final FlightSearchService searchService;

    @GetMapping("/search")
    public List<Flight> search(@RequestParam String origin, @RequestParam String destination, @RequestParam String date) {
        return searchService.searchFlights(origin, destination, date);
    }

    @GetMapping("/{id}/availability")
    public boolean check(@PathVariable Long id, @RequestParam int seats) {
        return searchService.getFlightById(id).getAvailableSeats() >= seats;
    }

    @PostMapping("/seed")
    public Flight seed(@RequestBody Flight flight) {
        return searchService.updateFlight(flight);
    }
}
