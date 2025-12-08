package com.flight.booking.inventory.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "app-exchange";

    public static final String BOOKING_CREATED_QUEUE = "booking.created.queue";
    public static final String INVENTORY_BLOCKED_QUEUE = "inventory.blocked.queue";
    public static final String INVENTORY_FAILED_QUEUE = "inventory.failed.queue";

    public static final String BOOKING_CREATED_RK = "booking.created";
    public static final String INVENTORY_BLOCKED_RK = "inventory.blocked";
    public static final String INVENTORY_FAILED_RK = "inventory.failed";

    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue bookingCreatedQueue() {
        return QueueBuilder.durable(BOOKING_CREATED_QUEUE).build();
    }

    @Bean
    public Queue inventoryBlockedQueue() {
        return QueueBuilder.durable(INVENTORY_BLOCKED_QUEUE).build();
    }

    @Bean
    public Queue inventoryFailedQueue() {
        return QueueBuilder.durable(INVENTORY_FAILED_QUEUE).build();
    }

    @Bean
    public Binding bookingCreatedBinding(Queue bookingCreatedQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(bookingCreatedQueue).to(appExchange).with(BOOKING_CREATED_RK);
    }

    @Bean
    public Binding inventoryBlockedBinding(Queue inventoryBlockedQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(inventoryBlockedQueue).to(appExchange).with(INVENTORY_BLOCKED_RK);
    }

    @Bean
    public Binding inventoryFailedBinding(Queue inventoryFailedQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(inventoryFailedQueue).to(appExchange).with(INVENTORY_FAILED_RK);
    }
}
