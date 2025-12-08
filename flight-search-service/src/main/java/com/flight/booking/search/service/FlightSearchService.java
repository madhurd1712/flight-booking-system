package com.flight.booking.search.service;

import com.flight.booking.search.entity.Flight;
import com.flight.booking.search.repository.FlightRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class FlightSearchService {

    @Autowired
    private FlightRepository flightRepository;

    /**
     * Cache search results by source, destination, and date
     * Cache key format: "source:destination:date"
     */
    @Cacheable(value = "flightSearchCache",
            key = "#source + ':' + #destination + ':' + #departureDate",
            unless = "#result == null || #result.isEmpty()")
    public List<Flight> searchFlights(String source, String destination, String departureDate) {
        log.info("Fetching flights from database for: {} -> {} on {}", source, destination, departureDate);

        LocalDateTime start = LocalDateTime.parse(departureDate + "T00:00:00");
        LocalDateTime end = LocalDateTime.parse(departureDate + "T23:59:59");

        // This will only execute if cache miss
        return flightRepository.findByOriginAndDestinationAndDepartureTimeBetween(
                source, destination, start, end
        );
    }

    /**
     * Cache individual flight by ID
     */
    @Cacheable(value = "flightCache", key = "#flightId", unless = "#result == null")
    public Flight getFlightById(String flightId) {
        log.info("Fetching flight from database with ID: {}", flightId);

        return flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with ID: " + flightId));
    }

    /**
     * Cache all flights (use carefully, might be large dataset)
     */
    @Cacheable(value = "allFlightsCache", unless = "#result == null || #result.isEmpty()")
    public List<Flight> getAllFlights() {
        log.info("Fetching all flights from database");
        return flightRepository.findAll();
    }

    /**
     * Evict specific search cache when flight is updated
     */
    @CacheEvict(value = {"flightSearchCache", "flightCache", "allFlightsCache"}, allEntries = true)
    public Flight updateFlight(Flight flight) {
        log.info("Updating flight and clearing cache");
        return flightRepository.save(flight);
    }

    /**
     * Evict cache when new flight is added
     */
    @CacheEvict(value = {"flightSearchCache", "allFlightsCache"}, allEntries = true)
    public Flight addFlight(Flight flight) {
        log.info("Adding new flight and clearing search cache");
        return flightRepository.save(flight);
    }

    /**
     * Clear all caches manually
     */
    @CacheEvict(value = {"flightSearchCache", "flightCache", "allFlightsCache"}, allEntries = true)
    public void clearAllCaches() {
        log.info("Manually clearing all flight caches");
    }
}
