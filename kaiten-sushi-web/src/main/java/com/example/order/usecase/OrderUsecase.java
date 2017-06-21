package com.example.order.usecase;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.example.order.domain.event.OrderCheckedOutEvent;
import com.example.order.domain.event.OrderItemDeliveredEvent;
import com.example.order.domain.event.StoredEvent;
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
public class OrderUsecase {

  private OrderRepository orderRepository;

  private OrderAddService orderAddService;

  private KafkaTemplate<String, StoredEvent> kafkaTemplate;

  @Autowired
  public OrderUsecase(OrderRepository orderRepository, OrderAddService orderAddService,
      KafkaTemplate<String, StoredEvent> kafkaTemplate) {
    this.orderRepository = orderRepository;
    this.orderAddService = orderAddService;
    this.kafkaTemplate = kafkaTemplate;
  }

  public void addOrder(Integer guestId, String guestName, String productId, Integer quantity) {
    orderAddService.addOrder(new OrderGuestId(guestId), new OrderGuestName(guestName), new ProductId(productId),
        new OrderQuantity(quantity));
  }

  public void checkout(Integer guestId) {
    OrderGroup orderGroup = orderRepository.activeOrderGroupOf(new OrderGuestId(guestId));
    Assert.notNull(orderGroup);
    orderGroup.checkout();
    orderRepository.save(orderGroup);

    kafkaTemplate.send("topic1", new StoredEvent(new OrderCheckedOutEvent(guestId)));
  }

  public void deliverOrderItem(String orderItemId, Integer deliveryPersonId, String deliveryPersonName) {
    OrderGroup orderGroup = orderRepository.orderGroupOfOrderItemId(new OrderItemId(orderItemId));
    DeliveryDateTime deliveredOn = new DeliveryDateTime(LocalDateTime.now());
    orderGroup.deliverOrderItem(new OrderItemId(orderItemId), new DeliveryPersonId(deliveryPersonId),
        new DeliveryPersonName(deliveryPersonName), deliveredOn);
    orderRepository.save(orderGroup);

    kafkaTemplate.send("topic1", new StoredEvent(
        new OrderItemDeliveredEvent(orderItemId, deliveryPersonId, deliveryPersonName, deliveredOn.getValue())));
  }

}
