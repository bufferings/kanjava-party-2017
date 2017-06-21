package com.example.order.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.example.order.domain.event.OrderDeliveredEvent;
import com.example.order.domain.event.OrderGroupCheckedOutEvent;
import com.example.order.domain.event.StoredEvent;
import com.example.order.domain.model.OrderAddService;
import com.example.order.domain.model.order.OrderGroup;
import com.example.order.domain.model.order.OrderGroupId;
import com.example.order.domain.model.order.OrderGuestId;
import com.example.order.domain.model.order.OrderGuestName;
import com.example.order.domain.model.order.OrderId;
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

    kafkaTemplate.send("topic1", new StoredEvent(new OrderGroupCheckedOutEvent(orderGroup.getId().getValue())));
  }

  public void deliverOrder(String orderGroupId, String orderId) {
    OrderGroup orderGroup = orderRepository.orderGroupOfId(new OrderGroupId(orderGroupId));
    orderGroup.deliverOrder(new OrderId(orderId));
    orderRepository.save(orderGroup);

    kafkaTemplate.send("topic1", new StoredEvent(new OrderDeliveredEvent(orderGroupId, orderId)));
  }

}
