package com.example.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import com.example.myserdes.GenericAvroSerdeWithSchemaName;
import com.example.myserdes.KafkaAvroSerializerWithSchemaName;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaClientConfig {

  private static final String SCHEMA_REGISTRY_URL_KEY = "schema.registry.url";

  private final String schemaRegistryUrl;

  private final KafkaProperties properties;

  @Autowired
  public KafkaClientConfig(@Value("${schema.registry.url}") String schemaRegistryUrl, KafkaProperties properties) {
    this.schemaRegistryUrl = schemaRegistryUrl;
    this.properties = properties;
  }

  @Bean
  public ProducerFactory<?, ?> kafkaProducerFactory() {
    Map<String, Object> producerProperties = properties.buildProducerProperties();
    // For Avro
    producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializerWithSchemaName.class);
    producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializerWithSchemaName.class);
    // Schema Registry
    producerProperties.put(SCHEMA_REGISTRY_URL_KEY, schemaRegistryUrl);
    return new DefaultKafkaProducerFactory<Object, Object>(producerProperties);
  }

  @Bean
  public ConsumerFactory<?, ?> kafkaConsumerFactory() {
    Map<String, Object> consumerProperties = properties.buildConsumerProperties();
    // For Avro
    consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
    consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
    // Schema Registry
    consumerProperties.put(SCHEMA_REGISTRY_URL_KEY, schemaRegistryUrl);
    return new DefaultKafkaConsumerFactory<Object, Object>(consumerProperties);
  }

  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  public StreamsConfig kStreamsConfigs() {
    Map<String, Object> streamProperties = new HashMap<>();
    streamProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "order-srvice");
    // Utilizing the AutoConfiguration of Producer/Consumer
    streamProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
    // For Avro
    streamProperties.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, GenericAvroSerdeWithSchemaName.class.getName());
    streamProperties.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, GenericAvroSerdeWithSchemaName.class.getName());
    streamProperties.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
    // Commit every 1 sec for checking ( though I don't understand what's
    // "commit")
    streamProperties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "1000");
    // Schema Registry
    streamProperties.put(SCHEMA_REGISTRY_URL_KEY, schemaRegistryUrl);
    return new StreamsConfig(streamProperties);
  }

}
