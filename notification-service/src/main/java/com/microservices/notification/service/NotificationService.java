package com.microservices.notification.service;

import com.microservices.common.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    // On retire l'annotation @KafkaListener ici car elle est déjà dans OrderEventListener
    public void processOrderNotification(OrderEvent event) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📧 NOTIFICATION — Nouvelle commande reçue");
        log.info("   Commande ID  : {}", event.getOrderId());
        log.info("   Produit      : {} (id={})", event.getProductName(), event.getProductId());
        log.info("   Destinataire : {}", event.getCustomerEmail());
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        sendEmailNotification(event);
    }

    private void sendEmailNotification(OrderEvent event) {
        log.info("✉️  [SIMULATION] Email envoyé à {} — Commande #{} confirmée !",
                event.getCustomerEmail(), event.getOrderId());
    }
}