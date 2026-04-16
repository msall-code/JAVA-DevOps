package com.microservices.notification.config;

import com.microservices.common.event.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.lang.NonNull; // Import indispensable pour la Null Safety

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    @NonNull
    public ConsumerFactory<String, OrderEvent> consumerFactory() {
        // Configuration explicite du déserialiseur JSON
        JsonDeserializer<OrderEvent> payloadDeserializer = new JsonDeserializer<>(OrderEvent.class);
        payloadDeserializer.addTrustedPackages("com.microservices.common.event");
        payloadDeserializer.setUseTypeHeaders(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");
        
        // Utilisation des classes directement
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        
        // On définit le délégué pour l'ErrorHandlingDeserializer
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // On passe les instances de désérialiseurs directement au constructeur 
        // pour éviter les "unchecked conversion" lors de l'initialisation interne
        return new DefaultKafkaConsumerFactory<>(
                props, 
                new StringDeserializer(), 
                new ErrorHandlingDeserializer<>(payloadDeserializer)
        );
    }

    @Bean
    @NonNull
    public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> kafkaListenerContainerFactory(
            @NonNull ConsumerFactory<String, OrderEvent> consumerFactory) {
        
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}