package com.tours.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange za tour događaje
    public static final String TOUR_EVENTS_EXCHANGE = "tour-events-exchange";
    
    // Queue za RPC komunikaciju (Tours → Stakeholders)
    public static final String RPC_TOUR_QUEUE = "rpc-tour-queue";
    public static final String RPC_REPLY_TOUR_QUEUE = "rpc-reply-tour-queue";
    
    // Queues za event-driven komunikaciju (da se vide poruke u UI)
    public static final String TOUR_CREATED_QUEUE = "tour.created.queue";
    public static final String TOUR_DELETED_QUEUE = "tour.deleted.queue";
    public static final String TOUR_PUBLISHED_QUEUE = "tour.published.queue";

    @Bean
    public DirectExchange tourEventsExchange() {
        return new DirectExchange(TOUR_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue rpcTourQueue() {
        return new Queue(RPC_TOUR_QUEUE, false);
    }

    @Bean
    public Queue rpcReplyTourQueue() {
        return new Queue(RPC_REPLY_TOUR_QUEUE, false);
    }
    
    // Event queues (durable, čuva poruke)
    @Bean
    public Queue tourCreatedQueue() {
        return QueueBuilder.durable(TOUR_CREATED_QUEUE).build();
    }
    
    @Bean
    public Queue tourDeletedQueue() {
        return QueueBuilder.durable(TOUR_DELETED_QUEUE).build();
    }
    
    @Bean
    public Queue tourPublishedQueue() {
        return QueueBuilder.durable(TOUR_PUBLISHED_QUEUE).build();
    }
    
    // Bindings za evente
    @Bean
    public Binding bindingTourCreated() {
        return BindingBuilder
            .bind(tourCreatedQueue())
            .to(tourEventsExchange())
            .with("tour.created");
    }
    
    @Bean
    public Binding bindingTourDeleted() {
        return BindingBuilder
            .bind(tourDeletedQueue())
            .to(tourEventsExchange())
            .with("tour.deleted");
    }
    
    @Bean
    public Binding bindingTourPublished() {
        return BindingBuilder
            .bind(tourPublishedQueue())
            .to(tourEventsExchange())
            .with("tour.published");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setReplyTimeout(60000); // 60 sekundi timeout za RPC
        return template;
    }
}

