package com.tours.events;

import com.tours.config.RabbitMQConfig;
import com.tours.model.Tour;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Publikuje tour događaje preko RabbitMQ
 */
@Service
public class TourEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public TourEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Šalje event kada se kreira nova tura
     */
    public void publishTourCreatedEvent(Tour tour) {
        System.out.println("📤 Šaljem TourCreatedEvent - Tura ID: " + tour.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.TOUR_EVENTS_EXCHANGE,
            "tour.created",
            tour
        );
    }

    /**
     * Šalje event kada se tura obriše
     */
    public void publishTourDeletedEvent(Long tourId) {
        System.out.println("📤 Šaljem TourDeletedEvent - Tura ID: " + tourId);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.TOUR_EVENTS_EXCHANGE,
            "tour.deleted",
            tourId
        );
    }

    /**
     * Šalje event kada se tura objavi
     */
    public void publishTourPublishedEvent(Tour tour) {
        System.out.println("📤 Šaljem TourPublishedEvent - Tura ID: " + tour.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.TOUR_EVENTS_EXCHANGE,
            "tour.published",
            tour
        );
    }
}


