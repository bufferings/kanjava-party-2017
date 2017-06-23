package com.example.myserde;

import java.util.Map;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import io.confluent.kafka.streams.serdes.avro.GenericAvroDeserializer;

public class GenericAvroSerdeWithSchemaName implements Serde<GenericRecord> {

  private final Serde<GenericRecord> inner;

  public GenericAvroSerdeWithSchemaName() {
    inner = Serdes.serdeFrom(new GenericAvroSerializerWithSchemaName(), new GenericAvroDeserializer());
  }

  @Override
  public Serializer<GenericRecord> serializer() {
    return inner.serializer();
  }

  @Override
  public Deserializer<GenericRecord> deserializer() {
    return inner.deserializer();
  }

  @Override
  public void configure(final Map<String, ?> serdeConfig, final boolean isSerdeForRecordKeys) {
    inner.serializer().configure(serdeConfig, isSerdeForRecordKeys);
    inner.deserializer().configure(serdeConfig, isSerdeForRecordKeys);
  }

  @Override
  public void close() {
    inner.serializer().close();
    inner.deserializer().close();
  }

}