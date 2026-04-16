package com.microservices.notification.listener;

import com.microservices.common.event.OrderEvent;        // ✅ IMPORTANT

import com.microservices.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "order-events", groupId = "notification-group")
    public void handleOrderEvent(OrderEvent event) {
        log.info("📥 Événement Kafka reçu pour la commande #{}", event.getOrderId());
        notificationService.processOrderNotification(event);
    }
}