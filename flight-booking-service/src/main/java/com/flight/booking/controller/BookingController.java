package com.flight.booking.controller;

import com.flight.booking.dto.BookingRequest;
import com.flight.booking.entity.Booking;
import com.flight.booking.service.BookingService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    @RateLimiter(name = "bookingApi", fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<Booking> book(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(service.createBooking(request));
    }

    public ResponseEntity<String> rateLimiterFallback(BookingRequest request, Throwable t) {
        return ResponseEntity.status(429).body("Too many requests");
    }
}
