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
import org.springframework.util.Assert;

import com.example.order.domain.model.order.OrderGroup;
import com.example.order.domain.model.order.OrderGroupId;
import com.example.order.domain.model.order.OrderGuestId;
import com.example.order.domain.model.order.OrderGuestName;
import com.example.order.domain.model.order.OrderItem;
import com.example.order.domain.model.order.OrderQuantity;
import com.example.order.domain.model.order.OrderRepository;
import com.example.order.domain.model.product.Product;
import com.example.order.domain.model.product.ProductId;
import com.example.order.domain.model.product.ProductRepository;
import com.example.order.util.IdUtil;

@Service
public class OrderAddService {

  private OrderRepository orderRepository;

  private ProductRepository productRepository;

  private KafkaTemplate<String, GenericRecord> kafkaTemplate;

  private Schema orderItemCreatedEventSchema;

  @Autowired
  public OrderAddService(OrderRepository orderRepository, ProductRepository productRepository,
      KafkaTemplate<String, GenericRecord> kafkaTemplate,
      @Value(value = "classpath:avro/OrderItemCreatedEvent.avsc") Resource orderItemCreatedEventSchemaFile) {
    this.orderRepository = orderRepository;
    this.productRepository = productRepository;
    this.kafkaTemplate = kafkaTemplate;

    try {
      try (InputStream is = orderItemCreatedEventSchemaFile.getInputStream()) {
        orderItemCreatedEventSchema = new Schema.Parser().parse(is);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void addOrder(OrderGuestId guestId, OrderGuestName guestName, ProductId productId, OrderQuantity quantity) {

    Product product = productRepository.productOfId(productId);
    Assert.notNull(product);
    Assert.isTrue(product.canKeepStockForOrder(quantity));

    OrderGroup orderGroup = orderRepository.activeOrderGroupOf(guestId);
    if (orderGroup == null) {
      OrderGroupId orderGroupId = new OrderGroupId(IdUtil.generateId());
      orderGroup = OrderGroup.newOrderGroup(orderGroupId, guestId, guestName);
    }

    OrderItem newOrder = orderGroup.addOrderItem(productId, quantity);
    product.keepStockForOrder(quantity);

    orderRepository.save(orderGroup);
    productRepository.save(product);

    GenericRecord orderItemCreatedEvent = new GenericData.Record(orderItemCreatedEventSchema);
    orderItemCreatedEvent.put("orderGroupId", orderGroup.getId().getValue());
    orderItemCreatedEvent.put("orderItemId", newOrder.getId().getValue());
    orderItemCreatedEvent.put("orderGuestId", orderGroup.getOrderGuestId().getValue());
    orderItemCreatedEvent.put("orderGuestName", orderGroup.getOrderGuestName().getValue());
    orderItemCreatedEvent.put("productId", product.getId().getValue());
    orderItemCreatedEvent.put("productName", product.getName().getValue());
    orderItemCreatedEvent.put("quantity", newOrder.getQuantity().getValue());
    orderItemCreatedEvent.put("orderedOn", newOrder.getOrderedOn().getValue().toString());
    kafkaTemplate.send("order-topic", orderItemCreatedEvent);

  }
}
