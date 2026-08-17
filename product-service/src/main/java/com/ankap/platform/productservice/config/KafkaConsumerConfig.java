package com.ankap.platform.productservice.config;

import com.ankap.platform.productservice.dto.InventoryReleaseRequestedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

/**
 * application.properties pins a single default payload type for JSON consumers
 * (OrderPlacedEvent) with type headers switched off, so every additional payload
 * type needs a container factory that names its own target type.
 */
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReleaseRequestedEvent>
    inventoryReleaseListenerContainerFactory(KafkaProperties kafkaProperties) {

        JsonDeserializer<InventoryReleaseRequestedEvent> payloadDeserializer =
                new JsonDeserializer<>(InventoryReleaseRequestedEvent.class);
        payloadDeserializer.setUseTypeHeaders(false);
        payloadDeserializer.addTrustedPackages("*");

        Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.remove(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG);

        // The delegate above is configured in code, so the spring.json.* and
        // spring.deserializer.* properties must not also be present — Spring Kafka
        // rejects a deserializer configured both ways.
        props.keySet().removeIf(key -> key.startsWith("spring.json.") || key.startsWith("spring.deserializer."));

        ConsumerFactory<String, InventoryReleaseRequestedEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(
                        props,
                        new StringDeserializer(),
                        new ErrorHandlingDeserializer<>(payloadDeserializer));

        ConcurrentKafkaListenerContainerFactory<String, InventoryReleaseRequestedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
