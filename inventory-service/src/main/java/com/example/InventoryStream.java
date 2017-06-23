package com.example;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.example.event.InventoryRecoveredEvent;
import com.example.event.InventoryRecoveredEventSchema;
import com.example.event.InventoryRecoveryRequestedEvent;
import com.example.event.InventoryRecoveryRequestedEventSchema;
import com.example.event.InventoryReservationFailedEvent;
import com.example.event.InventoryReservationFailedEventSchema;
import com.example.event.InventoryReservationRequestedEvent;
import com.example.event.InventoryReservationRequestedEventSchema;
import com.example.event.InventoryReservedEvent;
import com.example.event.InventoryReservedEventSchema;
import com.example.myserde.GenericAvroSerdeWithSchemaName;
import com.example.store.ProductInventory;
import com.example.store.ProductInventorySchema;

@Component
public class InventoryStream {

  private static final String INVENTORY_STORE_NAME = "InventoryStore";

  private static final String INVENTORY_TOPIC = "inventory-topic";

  private static final String INVENTORY_RESULT_TOPIC = "inventory-result-topic";

  private final InventoryRecoveredEventSchema inventoryRecoveredEventSchema;

  private final InventoryRecoveryRequestedEventSchema inventoryRecoveryRequestedEventSchema;

  private final InventoryReservationFailedEventSchema inventoryReservationFailedEventSchema;

  private final InventoryReservationRequestedEventSchema inventoryReservationRequestedEventSchema;

  private final InventoryReservedEventSchema inventoryReservedEventSchema;

  private final ProductInventorySchema productInventorySchema;

  @Autowired
  public InventoryStream(InventoryRecoveredEventSchema inventoryRecoveredEventSchema,
      InventoryRecoveryRequestedEventSchema inventoryRecoveryRequestedEventSchema,
      InventoryReservationFailedEventSchema inventoryReservationFailedEventSchema,
      InventoryReservationRequestedEventSchema inventoryReservationRequestedEventSchema,
      InventoryReservedEventSchema inventoryReservedEventSchema, ProductInventorySchema productInventorySchema) {
    this.inventoryRecoveredEventSchema = inventoryRecoveredEventSchema;
    this.inventoryRecoveryRequestedEventSchema = inventoryRecoveryRequestedEventSchema;
    this.inventoryReservationFailedEventSchema = inventoryReservationFailedEventSchema;
    this.inventoryReservationRequestedEventSchema = inventoryReservationRequestedEventSchema;
    this.inventoryReservedEventSchema = inventoryReservedEventSchema;
    this.productInventorySchema = productInventorySchema;
  }

  @SuppressWarnings("rawtypes")
  @Bean
  public KStream<String, GenericRecord> kStream(StreamsConfig kStreamConfig, KStreamBuilder kStreamsBuilder) {
    StateStoreSupplier inventoryStoreSupplier = Stores.create(INVENTORY_STORE_NAME)
        .withKeys(Serdes.String())
        .withValues((GenericAvroSerdeWithSchemaName) kStreamConfig.valueSerde())
        .persistent()
        .build();
    kStreamsBuilder.addStateStore(inventoryStoreSupplier);

    KStream<String, GenericRecord> sourceStream = kStreamsBuilder.stream(INVENTORY_TOPIC);
    KStream<String, GenericRecord> resultStream = sourceStream.transformValues(InventoryValueTransformer::new,
        INVENTORY_STORE_NAME);
    resultStream.to(INVENTORY_RESULT_TOPIC);

    // No need to return object, @Bean seems just for timing.
    return null;
  }

  private class InventoryValueTransformer implements ValueTransformer<GenericRecord, GenericRecord> {

    private KeyValueStore<String, GenericRecord> inventoryStore;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void init(ProcessorContext context) {
      inventoryStore = (KeyValueStore) context.getStateStore(INVENTORY_STORE_NAME);
    }

    @Override
    public GenericRecord transform(GenericRecord value) {
      if (inventoryRecoveryRequestedEventSchema.schemaNameEqualsTo(value)) {
        InventoryRecoveryRequestedEvent event = inventoryRecoveryRequestedEventSchema.eventFrom(value);
        return handleInventoryRecoveryRequestedEvent(event);
      }

      if (inventoryReservationRequestedEventSchema.schemaNameEqualsTo(value)) {
        InventoryReservationRequestedEvent event = inventoryReservationRequestedEventSchema.eventFrom(value);
        return handleInventoryReservationRequestedEvent(event);
      }

      return null;
    }

    private GenericRecord handleInventoryRecoveryRequestedEvent(InventoryRecoveryRequestedEvent event) {
      ProductInventory entity = productInventorySchema.entityFrom(event.productId, inventoryStore.get(event.productId));
      entity.recover();
      inventoryStore.put(event.productId, productInventorySchema.recordFrom(entity));
      return inventoryRecoveredEventSchema.recordFrom(InventoryRecoveredEvent.from(event));
    }

    private GenericRecord handleInventoryReservationRequestedEvent(InventoryReservationRequestedEvent event) {
      ProductInventory entity = productInventorySchema.entityFrom(event.productId, inventoryStore.get(event.productId));
      if (entity.canAccept(event.reservationCount)) {
        entity.accept(event.reservationCount);
        inventoryStore.put(event.productId, productInventorySchema.recordFrom(entity));
        InventoryReservedEvent newEvent = InventoryReservedEvent.from(event);
        return inventoryReservedEventSchema.recordFrom(newEvent);
      } else {
        InventoryReservationFailedEvent newEvent = InventoryReservationFailedEvent.from(event);
        return inventoryReservationFailedEventSchema.recordFrom(newEvent);
      }
    }

    @Override
    public GenericRecord punctuate(long timestamp) {
      return null;
    }

    @Override
    public void close() {
      inventoryStore.close();
    }
  }
}
