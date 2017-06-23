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
public class InventoryRecoveryRequestedEventSchema {

  private static final String SCHEMA_RESOURCE = "classpath:avro/InventoryRecoveryRequestedEvent.avsc";

  private final Schema schema;

  @Autowired
  public InventoryRecoveryRequestedEventSchema(@Value(SCHEMA_RESOURCE) Resource schemaResource) throws IOException {
    try (InputStream is = schemaResource.getInputStream()) {
      schema = new Schema.Parser().parse(is);
    }
  }

  public InventoryRecoveryRequestedEvent eventFrom(GenericRecord record) {
    Assert.isTrue(schemaNameEqualsTo(record), "invalid schema");
    return InventoryRecoveryRequestedEvent.builder()
        .eventId(((Utf8) record.get("eventId")).toString())
        .productId(((Utf8) record.get("productId")).toString())
        .build();
  }

  public GenericRecord recordFrom(InventoryRecoveryRequestedEvent event) {
    GenericRecord record = new GenericData.Record(schema);
    record.put("eventId", event.eventId);
    record.put("productId", event.productId);
    return record;
  }

  public boolean schemaNameEqualsTo(GenericRecord record) {
    return schema.getFullName().equals(record.getSchema().getFullName());
  }

}
