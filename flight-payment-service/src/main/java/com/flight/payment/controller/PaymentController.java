package com.flight.payment.controller;

import com.flight.payment.dto.PaymentRequest;
import com.flight.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public String pay(@RequestBody PaymentRequest request) {
        return paymentService.process(request);
    }
}
