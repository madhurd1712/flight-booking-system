package com.flight.booking.notification.service;

import com.flight.booking.notification.dto.PaymentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    // simple in-memory or DB-backed processed-event store for idempotency
    public boolean isProcessed(String eventId) {
        // check DB table processed_events
        return false;
    }

    public void markProcessed(String eventId) {
        // persist processed event id
    }

    public void sendPaymentNotification(PaymentEvent event) {
        // send email/SMS (start with console log for now)
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

    // Commented for now, will implement after introducing BookingEvent
    /*public void sendBookingNotification(BookingEvent event) {
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
    }*/
}
