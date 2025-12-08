package com.flight.booking.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "app-exchange";
    public static final String BOOKING_CREATED_QUEUE = "booking.created.queue";
    public static final String BOOKING_CREATED_RK = "booking.created";

    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue bookingCreatedQueue() {
        return QueueBuilder.durable(BOOKING_CREATED_QUEUE).build();
    }

    @Bean
    public Binding bindBookingCreated() {
        return BindingBuilder.bind(bookingCreatedQueue()).to(appExchange()).with(BOOKING_CREATED_RK);
    }
}
