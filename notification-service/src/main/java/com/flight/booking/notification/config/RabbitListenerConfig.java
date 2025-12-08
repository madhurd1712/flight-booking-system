package com.flight.booking.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitListenerConfig {

    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange("app-exchange", true, false);
    }

    @Bean
    public Queue paymentNotificationQueue() {
        return new Queue("notification.payment", true);
    }

    @Bean
    public Binding paymentBinding(TopicExchange appExchange) {
        return BindingBuilder.bind(paymentNotificationQueue())
                .to(appExchange)
                .with("payment.*");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
