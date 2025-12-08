package com.flight.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // topic exchange so multiple services can subscribe
    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange("app-exchange");
    }

    // JSON converter so we can send POJOs
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // durable queue for payment service
    @Bean
    public Queue paymentInventoryBlockedQueue() {
        return QueueBuilder.durable("payment.inventory.blocked.queue")  .build();
    }

    // listen to routing key inventory.blocked
    @Bean
    public Binding bindInventoryBlocked(Queue paymentInventoryBlockedQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(paymentInventoryBlockedQueue).to(appExchange).with("inventory.blocked");
    }
}
