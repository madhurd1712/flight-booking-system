package com.flight.payment.service;

import com.flight.payment.dto.PaymentRequest;
import com.flight.payment.entity.Payment;
import com.flight.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository repo;
    private final RabbitTemplate rabbit;

    public String process(PaymentRequest request) {

        repo.save(Payment.builder().bookingId(request.getBookingId()).amount(request.getAmount()).status("SUCCESS").build());
        try {
            rabbit.convertAndSend("paymentQueue", "Payment Confirmed: " + request.getBookingId());
        } catch (Exception e) {
            System.err.println("RabbitMQ error");
        }

        return "Processed";
    }
}
