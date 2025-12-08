package com.flight.booking.notification.consumer;

import com.flight.booking.notification.dto.BookingEvent;
import com.flight.booking.notification.dto.PaymentEvent;
import com.flight.booking.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "notification.payment", durable = "true"),
            exchange = @Exchange(value = "app-exchange", type = "topic"),
            key = "payment.*"
    ))
    public void handlePaymentEvent(PaymentEvent event) {
        if (notificationService.isProcessed(event.getEventId())) return;
        notificationService.sendPaymentNotification(event);
        notificationService.markProcessed(event.getEventId());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "notification.booking", durable = "true"),
            exchange = @Exchange(value = "app-exchange", type = "topic"),
            key = "booking.*"
    ))
    public void handleBookingEvent(BookingEvent event) {
        if (notificationService.isProcessed(event.getEventId())) return;
        notificationService.sendBookingNotification(event);
        notificationService.markProcessed(event.getEventId());
    }
}
