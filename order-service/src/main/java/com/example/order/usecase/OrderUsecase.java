package com.example.order.usecase;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.order.domain.model.OrderAddService;
import com.example.order.domain.model.order.DeliveryDateTime;
import com.example.order.domain.model.order.DeliveryPersonId;
import com.example.order.domain.model.order.DeliveryPersonName;
import com.example.order.domain.model.order.OrderGroup;
import com.example.order.domain.model.order.OrderGuestId;
import com.example.order.domain.model.order.OrderGuestName;
import com.example.order.domain.model.order.OrderItemId;
import com.example.order.domain.model.order.OrderQuantity;
import com.example.order.domain.model.order.OrderRepository;
import com.example.order.domain.model.product.ProductId;

@Service
@Transactional
@RestController
public class OrderUsecase {

  private OrderRepository orderRepository;

  private OrderAddService orderAddService;

  private KafkaTemplate<String, GenericRecord> kafkaTemplate;

  private Schema orderItemDeliveredEventSchema;

  private Schema orderCheckedOutEventSchema;

  @Autowired
  public OrderUsecase(OrderRepository orderRepository, OrderAddService orderAddService,
      KafkaTemplate<String, GenericRecord> kafkaTemplate,
      @Value(value = "classpath:avro/OrderItemDeliveredEvent.avsc") Resource orderItemDeliveredEventSchemaFile,
      @Value(value = "classpath:avro/OrderCheckedOutEvent.avsc") Resource orderCheckedOutEventSchemaFile) {
    this.orderRepository = orderRepository;
    this.orderAddService = orderAddService;
    this.kafkaTemplate = kafkaTemplate;
    try {
      try (InputStream is = orderItemDeliveredEventSchemaFile.getInputStream()) {
        orderItemDeliveredEventSchema = new Schema.Parser().parse(is);
      }
      try (InputStream is = orderCheckedOutEventSchemaFile.getInputStream()) {
        orderCheckedOutEventSchema = new Schema.Parser().parse(is);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @PostMapping("order-items/add")
  public void addOrder(@RequestParam("orderGuestId") Integer orderGuestId,
      @RequestParam("orderGuestName") String orderGuestName, @RequestParam("productId") String productId,
      @RequestParam("quantity") Integer quantity) {
    orderAddService.addOrder(new OrderGuestId(orderGuestId), new OrderGuestName(orderGuestName),
        new ProductId(productId), new OrderQuantity(quantity));
  }

  @PostMapping("order-items/{orderItemId}/deliver")
  public void deliverOrderItem(@PathVariable("orderItemId") String orderItemId,
      @RequestParam("deiveryPersonId") Integer deliveryPersonId,
      @RequestParam("deliveryPersonName") String deliveryPersonName) {
    OrderGroup orderGroup = orderRepository.orderGroupOfOrderItemId(new OrderItemId(orderItemId));
    DeliveryDateTime deliveredOn = new DeliveryDateTime(LocalDateTime.now());
    orderGroup.deliverOrderItem(new OrderItemId(orderItemId), new DeliveryPersonId(deliveryPersonId),
        new DeliveryPersonName(deliveryPersonName), deliveredOn);
    orderRepository.save(orderGroup);

    GenericRecord orderItemDeliveredEvent = new GenericData.Record(orderItemDeliveredEventSchema);
    orderItemDeliveredEvent.put("orderItemId", orderItemId);
    orderItemDeliveredEvent.put("deliveryPersonId", deliveryPersonId);
    orderItemDeliveredEvent.put("deliveryPersonName", deliveryPersonName);
    orderItemDeliveredEvent.put("deliveredOn", deliveredOn.getValue().toString());
    kafkaTemplate.send("order-topic", orderItemDeliveredEvent);
  }

  @PostMapping("checkout")
  public void checkout(@RequestParam("orderGuestId") Integer orderGuestId) {
    OrderGroup orderGroup = orderRepository.activeOrderGroupOf(new OrderGuestId(orderGuestId));
    Assert.notNull(orderGroup);
    orderGroup.checkout();
    orderRepository.save(orderGroup);

    GenericRecord orderCheckedOutEvent = new GenericData.Record(orderCheckedOutEventSchema);
    orderCheckedOutEvent.put("orderGuestId", orderGuestId);
    kafkaTemplate.send("order-topic", orderCheckedOutEvent);
  }

}
