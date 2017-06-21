package com.example.order.usecase;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
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

  @PostMapping("order-items/add")
  public void addOrder(@RequestParam("orderGuestId") Integer orderGuestId,
      @RequestParam("orderGuestName") String orderGuestName, @RequestParam("productId") String productId,
      @RequestParam("quantity") Integer quantity) {
    orderAddService.addOrder(new OrderGuestId(orderGuestId), new OrderGuestName(orderGuestName),
        new ProductId(productId), new OrderQuantity(quantity));
  }

  @PostMapping("checkout")
  public void checkout(@RequestParam("orderGuestId") Integer orderGuestId) {
    OrderGroup orderGroup = orderRepository.activeOrderGroupOf(new OrderGuestId(orderGuestId));
    Assert.notNull(orderGroup);
    orderGroup.checkout();
    orderRepository.save(orderGroup);

    kafkaTemplate.send("topic1", new StoredEvent(new OrderCheckedOutEvent(orderGuestId)));
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

    kafkaTemplate.send("topic1", new StoredEvent(
        new OrderItemDeliveredEvent(orderItemId, deliveryPersonId, deliveryPersonName, deliveredOn.getValue())));
  }

}
