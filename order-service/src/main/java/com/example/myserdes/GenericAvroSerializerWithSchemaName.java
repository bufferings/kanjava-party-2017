package com.example.myserdes;

import java.util.Map;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serializer;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;

public class GenericAvroSerializerWithSchemaName implements Serializer<GenericRecord> {

  private final KafkaAvroSerializerWithSchemaName inner;

  public GenericAvroSerializerWithSchemaName() {
    inner = new KafkaAvroSerializerWithSchemaName();
  }

  /**
   * For testing purposes only.
   */
  GenericAvroSerializerWithSchemaName(final SchemaRegistryClient client) {
    inner = new KafkaAvroSerializerWithSchemaName(client);
  }

  @Override
  public void configure(final Map<String, ?> serializerConfig, final boolean isSerializerForRecordKeys) {
    inner.configure(serializerConfig, isSerializerForRecordKeys);
  }

  @Override
  public byte[] serialize(final String topic, final GenericRecord record) {
    return inner.serialize(topic, record);
  }

  @Override
  public void close() {
    inner.close();
  }

}