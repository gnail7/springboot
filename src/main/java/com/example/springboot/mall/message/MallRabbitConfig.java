package com.example.springboot.mall.message;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MallRabbitConfig {
    public static final String EXCHANGE = "mall.business.exchange";
    public static final String ORDER_CREATED_QUEUE = "mall.order.created.queue";
    public static final String DLX = "mall.dead-letter.exchange";
    public static final String DLQ = "mall.dead-letter.queue";

    @Bean
    DirectExchange mallBusinessExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    DirectExchange mallDeadLetterExchange() {
        return new DirectExchange(DLX, true, false);
    }

    @Bean
    Queue orderCreatedQueue() {
        return new Queue(ORDER_CREATED_QUEUE, true, false, false,
                java.util.Map.of("x-dead-letter-exchange", DLX, "x-dead-letter-routing-key", DLQ));
    }

    @Bean
    Queue deadLetterQueue() {
        return new Queue(DLQ, true);
    }

    @Bean
    Binding orderCreatedBinding(Queue orderCreatedQueue, DirectExchange mallBusinessExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(mallBusinessExchange).with("ORDER_CREATED");
    }

    @Bean
    Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange mallDeadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(mallDeadLetterExchange).with(DLQ);
    }
}
