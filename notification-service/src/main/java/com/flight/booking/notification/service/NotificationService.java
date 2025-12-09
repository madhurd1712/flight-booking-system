package com.flight.booking.notification.service;

import com.flight.booking.common.events.BookingEvent;
import com.flight.booking.common.events.PaymentEvent;
import com.flight.booking.common.idempotency.ProcessedEvent;
import com.flight.booking.common.idempotency.ProcessedEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ProcessedEventRepository eventRepository;

    public boolean isProcessed(String eventId) {
        return eventRepository.existsById(eventId);
    }

    public void markProcessed(String eventId) {
        ProcessedEvent evt = new ProcessedEvent();
        evt.setEventId(eventId);
        eventRepository.save(evt);
    }

    public void sendPaymentNotification(PaymentEvent event) {
        System.out.println("Send payment notification for booking " + event.getBookingId());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("test@example.com"); // hardcoded dummy
            message.setSubject("Payment Received for Booking");
            message.setText("Payment processed for booking: " + event.getBookingId());
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendBookingNotification(BookingEvent event) {
        System.out.println("Send booking notification for booking " + event.getBookingId());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("test@example.com"); // hardcoded dummy
            message.setSubject("Flight Booking Confirmed");
            message.setText("Booking confirmed. Booking ID: " + event.getBookingId());
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
