package com.flight.booking.inventory.consumer;

import com.flight.booking.inventory.dto.BookingEvent;
import com.flight.booking.inventory.dto.InventoryEvent;
import com.flight.booking.inventory.service.IdempotencyService;
import com.flight.booking.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Component
@RequiredArgsConstructor
public class BookingCreatedListener {

    private final InventoryService inventoryService;
    private final IdempotencyService idempotencyService;
    private final RabbitTemplate rabbit;

    @RabbitListener(queues = "booking.created.queue")
    public void onBookingCreated(BookingEvent bookingEvent) {
        if (idempotencyService.isProcessed(bookingEvent.getEventId())) {
            System.out.println("Duplicate Event Ignored: " + bookingEvent.getEventId());
            return;
        }

        boolean locked = inventoryService.lockSeats(bookingEvent.getFlightId(), bookingEvent.getSeats());
        if (locked) {
            // publish inventory.blocked event
            InventoryEvent out = InventoryEvent.builder()
                    .eventId(java.util.UUID.randomUUID().toString())
                    .bookingId(bookingEvent.getBookingId())
                    .flightId(bookingEvent.getFlightId())
                    .seats(bookingEvent.getSeats())
                    .amount(bookingEvent.getAmount())
                    .status("BLOCKED")
                    .timestamp(System.currentTimeMillis())
                    .build();
            rabbit.convertAndSend("app-exchange", "inventory.blocked", out);

            idempotencyService.markProcessed(bookingEvent.getEventId());
        } else {
            // publish inventory.failed event
            InventoryEvent out = InventoryEvent.builder()
                    .eventId(java.util.UUID.randomUUID().toString())
                    .bookingId(bookingEvent.getBookingId())
                    .flightId(bookingEvent.getFlightId())
                    .seats(bookingEvent.getSeats())
                    .amount(bookingEvent.getAmount())
                    .status("FAILED")
                    .timestamp(System.currentTimeMillis())
                    .build();
            rabbit.convertAndSend("app-exchange", "inventory.failed", out);
        }
    }
}