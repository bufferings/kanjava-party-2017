package com.example.event;

import java.io.IOException;
import java.io.InputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class InventoryReservedEventSchema {

  private static final String SCHEMA_RESOURCE = "classpath:avro/InventoryReservedEvent.avsc";

  private final Schema schema;

  @Autowired
  public InventoryReservedEventSchema(@Value(SCHEMA_RESOURCE) Resource schemaResource) throws IOException {
    try (InputStream is = schemaResource.getInputStream()) {
      schema = new Schema.Parser().parse(is);
    }
  }

  public String getFullName() {
    return schema.getFullName();
  }

  public InventoryReservedEvent eventFrom(GenericRecord record) {
    Assert.isTrue(schema.getFullName().equals(record.getSchema().getFullName()), "invalid schema");
    return InventoryReservedEvent.builder()
        .eventId(((Utf8) record.get("eventId")).toString())
        .productId(((Utf8) record.get("productId")).toString())
        .reservationCount((Integer) record.get("reservationCount"))
        .requestEventId(((Utf8) record.get("requestEventId")).toString())
        .build();
  }

  public GenericRecord recordFrom(InventoryReservedEvent event) {
    GenericRecord record = new GenericData.Record(schema);
    record.put("eventId", event.eventId);
    record.put("productId", event.productId);
    record.put("reservationCount", event.reservationCount);
    record.put("requestEventId", event.requestEventId);
    return record;
  }

}
