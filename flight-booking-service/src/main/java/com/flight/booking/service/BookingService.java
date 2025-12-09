package com.flight.booking.service;

import com.flight.booking.client.*;
import com.flight.booking.common.events.BookingEvent;
import com.flight.booking.common.events.InventoryEvent;
import com.flight.booking.common.idempotency.IdempotencyService;
import com.flight.booking.dto.*;
import com.flight.booking.entity.Booking;
import com.flight.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SearchClient search;
    private final IdempotencyService idempotencyService;
    private final RabbitTemplate rabbit;

    @Transactional
    public Booking createBooking(BookingRequest request) {
        if (!search.checkAvailability(request.getFlightId(), request.getSeats()))
            throw new RuntimeException("No seats");
        
        Booking booking = Booking.builder()
                .flightId(request.getFlightId())
                .userId(request.getUserId())
                .seats(request.getSeats())
                .amount(request.getAmount())
                .status("PENDING")
                .build();
        booking = bookingRepository.save(booking);

        BookingEvent bookingEvent = BookingEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .bookingId(booking.getId())
                .userId(booking.getUserId())
                .flightId(booking.getFlightId())
                .seats(booking.getSeats())
                .amount(booking.getAmount())
                .status("CREATED")
                .timestamp(Instant.now().toEpochMilli())
                .build();
        rabbit.convertAndSend("app-exchange", "booking.created", bookingEvent);

        return booking;
    }

    @RabbitListener(queues = "booking.inventory.failed.queue")
    public void onInventoryFailed(InventoryEvent evt) {
        if (idempotencyService.isProcessed(evt.getEventId())) return;

        Booking booking = bookingRepository.findById(evt.getBookingId()).get();
        booking.setStatus("FAILED");
        bookingRepository.save(booking);

        idempotencyService.markProcessed(evt.getEventId());
    }

}
