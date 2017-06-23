package com.example.admin;

import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.Builder;
import lombok.Data;

@Component
public class AllEventListener {

  @Builder
  @Data
  private static class HelloMessage {
    private final String topic;
    private final String schema;
    private final String record;
  }

  private static final Logger logger = LoggerFactory.getLogger(AllEventListener.class);

  private SimpMessagingTemplate template;

  @Autowired
  public AllEventListener(SimpMessagingTemplate template) {
    this.template = template;
  }

  @KafkaListener(topics = { "order-topic", "inventory-topic",
      "inventory-result-topic" }, containerFactory = AdminKafkaConfig.CONTAINER_NAME)
  public void listen(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Payload GenericRecord record) {
    HelloMessage message = HelloMessage.builder()
        .topic(topic)
        .schema(record.getSchema().getFullName())
        .record(record.toString())
        .build();
    logger.info(message.toString());
    this.template.convertAndSend("/topic/greetings", message);
  }

}