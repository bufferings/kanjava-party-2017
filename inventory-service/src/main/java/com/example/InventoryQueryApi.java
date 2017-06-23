package com.example;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KStreamBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.store.ProductInventory;
import com.example.store.ProductInventorySchema;

@RestController
public class InventoryQueryApi {

  private static final String INVENTORY_STORE_NAME = "InventoryStore";

  @Autowired
  private KStreamBuilderFactoryBean kStreamBuilder;

  @Autowired
  private ProductInventorySchema productInventorySchema;

  public InventoryQueryApi(KStreamBuilderFactoryBean kStreamBuilder, ProductInventorySchema productInventorySchema) {
    this.kStreamBuilder = kStreamBuilder;
    this.productInventorySchema = productInventorySchema;
  }

  @GetMapping("inventories")
  public List<ProductInventory> inventories() {
    final ReadOnlyKeyValueStore<String, GenericRecord> store = kStreamBuilder.getKafkaStreams()
        .store(INVENTORY_STORE_NAME, QueryableStoreTypes.keyValueStore());

    final List<ProductInventory> results = new ArrayList<>();
    final KeyValueIterator<String, GenericRecord> records = store.all();
    while (records.hasNext()) {
      KeyValue<String, GenericRecord> record = records.next();
      ProductInventory entity = productInventorySchema.entityFrom(record.key, record.value);
      results.add(entity);
    }
    return results;
  }
}
