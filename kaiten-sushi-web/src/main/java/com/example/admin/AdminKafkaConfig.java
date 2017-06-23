package com.example.admin;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;

@Configuration
public class AdminKafkaConfig {

  public static final String CONTAINER_NAME = "admin-container";

  public static final String GROUP_ID = "admin";

  private static final String SCHEMA_REGISTRY_URL_KEY = "schema.registry.url";

  private final String schemaRegistryUrl;

  private final KafkaProperties properties;

  @Autowired
  public AdminKafkaConfig(@Value("${schema.registry.url}") String schemaRegistryUrl, KafkaProperties properties) {
    this.schemaRegistryUrl = schemaRegistryUrl;
    this.properties = properties;
  }

  @Bean(name = CONTAINER_NAME)
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Object, Object>> kafkaListenerContainerFactoryA() {
    ConcurrentKafkaListenerContainerFactory<Object, Object> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
    containerFactory.setConsumerFactory(kafkaConsumerFactory());
    return containerFactory;
  }

  private ConsumerFactory<Object, Object> kafkaConsumerFactory() {
    Map<String, Object> consumerProperties = properties.buildConsumerProperties();
    consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
    consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    // For Avro
    consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
    // Schema Registry
    consumerProperties.put(SCHEMA_REGISTRY_URL_KEY, schemaRegistryUrl);
    return new DefaultKafkaConsumerFactory<Object, Object>(consumerProperties);
  }
}