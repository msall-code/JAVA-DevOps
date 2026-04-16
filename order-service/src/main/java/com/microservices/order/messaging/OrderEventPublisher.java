package com.microservices.order.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// Note : J'utilise un objet générique si OrderEvent n'est pas encore créé
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "order-events";

    public void publishOrderCreatedEvent(Object event, Long orderId) {
        log.info("🚀 [Kafka] Envoi de l'événement pour la commande #{}", orderId);
        kafkaTemplate.send(TOPIC, orderId.toString(), event);
    }
}