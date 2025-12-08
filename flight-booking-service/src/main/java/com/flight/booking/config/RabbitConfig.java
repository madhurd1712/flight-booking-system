package com.flight.booking.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "app-exchange";
    public static final String BOOKING_CREATED_QUEUE = "booking.created.queue";
    public static final String BOOKING_CREATED_ROUTING_KEY = "booking.created";
    public static final String BOOKING_INVENTORY_FAILED_QUEUE = "booking.inventory.failed.queue";
    public static final String INVENTORY_FAILED_ROUTING_KEY = "inventory.failed";

    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue bookingCreatedQueue() {
        return QueueBuilder.durable(BOOKING_CREATED_QUEUE).build();
    }

    @Bean
    public Binding bookingCreatedBinding() {
        return BindingBuilder.bind(bookingCreatedQueue()).to(appExchange()).with(BOOKING_CREATED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue bookingInventoryFailedQueue() {
        return QueueBuilder.durable(BOOKING_INVENTORY_FAILED_QUEUE).build();
    }

    @Bean
    public Binding bindInventoryFailed() {
        return BindingBuilder.bind(bookingInventoryFailedQueue())
                .to(appExchange()).with(INVENTORY_FAILED_ROUTING_KEY);
    }

}
