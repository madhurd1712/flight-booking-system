package com.flight.payment;

import com.flight.payment.dto.PaymentRequest;
import com.flight.payment.entity.Payment;
import com.flight.payment.repository.PaymentRepository;
import com.flight.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock PaymentRepository repo;
    @Mock RabbitTemplate rabbit;
    @InjectMocks PaymentService s;

    @Test void testProcess() {
        PaymentRequest request = new PaymentRequest(); request.setBookingId(1L);
        when(repo.save(any())).thenReturn(new Payment());
        s.process(request);
        verify(repo).save(any());
        verify(rabbit).convertAndSend(eq("paymentQueue"), anyString());
    }
}
