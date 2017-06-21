package com.example.guest.config;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.example.order.domain.event.StoredEvent;

@Configuration
public class GuestKafkaConsumerConfig {

  public static final String CONTAINER_NAME = "guest-container";

  public static final String GROUP_ID = "guest";

  @Autowired
  KafkaProperties properties;

  @Bean(name = CONTAINER_NAME)
  KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, StoredEvent>> kafkaListenerContainerFactoryA() {
    Map<String, Object> config = properties.buildConsumerProperties();
    config.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);

    ConsumerFactory<String, StoredEvent> cf = new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
        new JsonDeserializer<>(StoredEvent.class));

    ConcurrentKafkaListenerContainerFactory<String, StoredEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(cf);
    return factory;
  }

}