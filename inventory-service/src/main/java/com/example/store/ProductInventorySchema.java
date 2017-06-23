package com.example.store;

import java.io.IOException;
import java.io.InputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ProductInventorySchema {

  private static final String SCHEMA_RESOURCE = "classpath:avro/ProductInventory.avsc";

  private final Schema schema;

  @Autowired
  public ProductInventorySchema(@Value(SCHEMA_RESOURCE) Resource schemaResource) throws IOException {
    try (InputStream is = schemaResource.getInputStream()) {
      schema = new Schema.Parser().parse(is);
    }
  }

  public ProductInventory entityFrom(String productId, GenericRecord record) {
    if (record == null) {
      return ProductInventory.builder().productId(productId).inventoryCount(0).build();
    }
    Assert.isTrue(schema.getFullName().equals(record.getSchema().getFullName()), "invalid schema");
    return ProductInventory.builder()
        .productId(productId)
        .inventoryCount((Integer) record.get("inventoryCount"))
        .build();
  }

  public GenericRecord recordFrom(ProductInventory entity) {
    GenericRecord record = new GenericData.Record(schema);
    record.put("productId", entity.productId);
    record.put("inventoryCount", entity.inventoryCount);
    return record;
  }

  public boolean schemaNameEqualsTo(GenericRecord record) {
    return schema.getFullName().equals(record.getSchema().getFullName());
  }

}
