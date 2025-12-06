package com.flight.booking.client;

import com.flight.booking.dto.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "http://localhost:8083/api/payments")
public interface PaymentClient {
    @PostMapping
    String initiatePayment(@RequestBody PaymentRequest request);
}
