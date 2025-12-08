package com.flight.payment.config;

import org.springframework.amqp.core.TopicExchange;
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
}
