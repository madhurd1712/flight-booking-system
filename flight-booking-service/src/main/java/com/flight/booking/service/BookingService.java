package com.flight.booking.service;

import com.flight.booking.client.*;
import com.flight.booking.dto.*;
import com.flight.booking.entity.Booking;
import com.flight.booking.repository.BookingRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repo;
    private final SearchClient search;
    private final PaymentClient payment;

    @Transactional
    public Booking createBooking(BookingRequest request) {
        if (!search.checkAvailability(request.getFlightId(), request.getSeats()))
            throw new RuntimeException("No seats");
        
        Booking booking = Booking.builder().flightId(request.getFlightId()).userId(request.getUserId())
                .seats(request.getSeats()).amount(request.getAmount()).status("PENDING").build();
        booking = repo.save(booking);
        processPaymentSafely(booking);
        return booking;
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "circuitBreakerFallBack")
    public void processPaymentSafely(Booking booking) {
        payment.initiatePayment(new PaymentRequest(booking.getId(), booking.getAmount()));
    }

    public void circuitBreakerFallBack(Booking booking, Throwable t) {
        System.err.println("Payment fallback for " + booking.getId());
        booking.setStatus("PAYMENT_RETRY");
        repo.save(booking);
    }
}
