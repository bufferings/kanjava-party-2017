package com.example;

import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.event.InventoryReservationRequestedEvent;
import com.example.event.InventoryReservationRequestedEventSchema;
import com.example.util.IdUtil;

@RestController
public class InventoryRequestApi {

  private static final String INVENTORY_TOPIC = "inventory-topic";

  private final KafkaTemplate<String, GenericRecord> kafkaTemplate;

  private final InventoryReservationRequestedEventSchema schema;

  @Autowired
  public InventoryRequestApi(KafkaTemplate<String, GenericRecord> kafkaTemplate,
      InventoryReservationRequestedEventSchema schema) {
    this.kafkaTemplate = kafkaTemplate;
    this.schema = schema;
  }

  @PostMapping("request")
  public void request(@RequestParam("productId") String productId,
      @RequestParam("reservationCount") Integer reservationCount) {
    InventoryReservationRequestedEvent event = InventoryReservationRequestedEvent.builder()
        .eventId(IdUtil.generateId())
        .productId(productId)
        .reservationCount(reservationCount)
        .build();
    kafkaTemplate.send(INVENTORY_TOPIC, productId, schema.recordFrom(event));
  }

}
