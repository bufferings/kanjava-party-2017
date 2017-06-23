package com.example.order.domain.model;

import java.io.IOException;
import java.io.InputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.order.domain.model.order.OrderGroup;
import com.example.order.domain.model.order.OrderGroupId;
import com.example.order.domain.model.order.OrderGuestId;
import com.example.order.domain.model.order.OrderGuestName;
import com.example.order.domain.model.order.OrderItem;
import com.example.order.domain.model.order.OrderQuantity;
import com.example.order.domain.model.order.OrderRepository;
import com.example.order.domain.model.product.ProductId;
import com.example.order.util.IdUtil;

@Service
public class OrderAddService {

  private OrderRepository orderRepository;

  private KafkaTemplate<String, GenericRecord> kafkaTemplate;

  private Schema orderItemCreatedEventSchema;

  @Autowired
  public OrderAddService(OrderRepository orderRepository, KafkaTemplate<String, GenericRecord> kafkaTemplate,
      @Value(value = "classpath:avro/OrderItemCreatedEvent.avsc") Resource orderItemCreatedEventSchemaFile)
      throws IOException {
    this.orderRepository = orderRepository;
    this.kafkaTemplate = kafkaTemplate;
    try (InputStream is = orderItemCreatedEventSchemaFile.getInputStream()) {
      orderItemCreatedEventSchema = new Schema.Parser().parse(is);
    }
  }

  public void addOrder(OrderGuestId guestId, OrderGuestName guestName, ProductId productId, OrderQuantity quantity) {
    OrderGroup orderGroup = orderRepository.activeOrderGroupOf(guestId);
    if (orderGroup == null) {
      OrderGroupId orderGroupId = new OrderGroupId(IdUtil.generateId());
      orderGroup = OrderGroup.newOrderGroup(orderGroupId, guestId, guestName);
    }

    OrderItem newOrder = orderGroup.addOrderItem(productId, quantity);
    orderRepository.save(orderGroup);

    GenericRecord orderItemCreatedEvent = new GenericData.Record(orderItemCreatedEventSchema);
    orderItemCreatedEvent.put("orderGroupId", orderGroup.getId().getValue());
    orderItemCreatedEvent.put("orderItemId", newOrder.getId().getValue());
    orderItemCreatedEvent.put("orderGuestId", orderGroup.getOrderGuestId().getValue());
    orderItemCreatedEvent.put("orderGuestName", orderGroup.getOrderGuestName().getValue());
    orderItemCreatedEvent.put("productId", productId.getValue());
    orderItemCreatedEvent.put("quantity", newOrder.getQuantity().getValue());
    orderItemCreatedEvent.put("orderedOn", newOrder.getOrderedOn().getValue().toString());
    kafkaTemplate.send("order-topic", orderItemCreatedEvent);

  }
}
