package com.flight.payment.consumer;

import com.flight.payment.dto.InventoryEvent;
import com.flight.payment.dto.PaymentRequest;
import com.flight.payment.service.IdempotencyService;
import com.flight.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentInventoryBlockedListner {

    private final PaymentService paymentService;
    private final IdempotencyService idempotencyService;
    private final RabbitTemplate rabbit;

    /**
     * Listens for inventory.blocked events and triggers payment processing.
     * This is synchronous: it constructs PaymentRequest from the event and calls process(...)
     */
    @RabbitListener(queues = "payment.inventory.blocked.queue")
    public void onInventoryBlocked(InventoryEvent inventoryEvent) {
        System.out.println("Received InventoryEvent for booking id: " + inventoryEvent.getBookingId() + " — starting payment");

        if (idempotencyService.isProcessed(inventoryEvent.getEventId())) {
            System.out.println("Duplicate Event Ignored: " + inventoryEvent.getEventId());
            return;
        }

        if (!"BLOCKED".equalsIgnoreCase(inventoryEvent.getStatus())) {
            System.out.println("InventoryEvent ignored (status != BLOCKED): " + inventoryEvent.getStatus());
            return;
        }

        try {
            PaymentRequest req = new PaymentRequest();
            req.setBookingId(inventoryEvent.getBookingId());
            req.setAmount(inventoryEvent.getAmount());

            paymentService.process(req);

            idempotencyService.markProcessed(inventoryEvent.getEventId());
        } catch (Exception ex) {
            System.err.println("Error handling InventoryEvent: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
