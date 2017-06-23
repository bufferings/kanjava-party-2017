package com.example;

import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.event.InventoryRecoveryRequestedEvent;
import com.example.event.InventoryRecoveryRequestedEventSchema;
import com.example.util.IdUtil;

@Component
@EnableScheduling
public class InventoryRecoveryService {

  private static final String INVENTORY_TOPIC = "inventory-topic";

  private final KafkaTemplate<String, GenericRecord> kafkaTemplate;

  private final InventoryRecoveryRequestedEventSchema schema;

  public InventoryRecoveryService(KafkaTemplate<String, GenericRecord> kafkaTemplate,
      InventoryRecoveryRequestedEventSchema schema) {
    this.kafkaTemplate = kafkaTemplate;
    this.schema = schema;
  }

  /**
   * Recovery inventory of products for demo use.
   */
  @Scheduled(fixedRate = 60 * 1000)
  public void recoverInventory() {
    send("3926d0ad-5638-4ed1-b16a-b09197ddcc10");
    send("1d8bcd93-2b9c-4225-a525-8f750d4c444c");
    send("7e570f08-65bd-4b1f-be75-3d977d818023");
  }

  private void send(String productId) {
    InventoryRecoveryRequestedEvent event = InventoryRecoveryRequestedEvent.builder()
        .eventId(IdUtil.generateId())
        .productId(productId)
        .build();
    kafkaTemplate.send(INVENTORY_TOPIC, productId, schema.recordFrom(event));
  }

}
