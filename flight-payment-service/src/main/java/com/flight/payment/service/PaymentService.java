package com.flight.payment.service;

import com.flight.booking.common.events.PaymentEvent;
import com.flight.payment.dto.PaymentRequest;
import com.flight.payment.entity.Payment;
import com.flight.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository repo;
    private final RabbitTemplate rabbit;

    public String process(PaymentRequest request) {

        Payment saved = repo.save(Payment.builder().bookingId(request.getBookingId())
                .amount(request.getAmount()).status("SUCCESS").build());
        try {
            PaymentEvent paymentEvent = PaymentEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .paymentId(saved.getId().toString())
                    .bookingId(request.getBookingId())
                    .userId(request.getUserId())
                    .amount(request.getAmount())
                    .status("SUCCEEDED")
                    .timestamp(Instant.now().toEpochMilli())
                    .build();

            rabbit.convertAndSend("app-exchange", "payment.succeeded", paymentEvent);
        } catch (Exception e) {
            System.err.println("RabbitMQ error: " + e.getMessage());
        }

        return "Processed";
    }
}
