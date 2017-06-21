package com.example.order.domain.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.example.order.domain.event.OrderItemCreatedEvent;
import com.example.order.domain.event.StoredEvent;
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

  private KafkaTemplate<String, StoredEvent> kafkaTemplate;

  @Autowired
  public OrderAddService(OrderRepository orderRepository, ProductRepository productRepository,
      KafkaTemplate<String, StoredEvent> kafkaTemplate) {
    this.orderRepository = orderRepository;
    this.productRepository = productRepository;
    this.kafkaTemplate = kafkaTemplate;
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

    kafkaTemplate.send("topic1",
        new StoredEvent(new OrderItemCreatedEvent(newOrder.getId().getValue(), orderGroup.getId().getValue(),
            orderGroup.getOrderGuestId().getValue(), orderGroup.getOrderGuestName().getValue(),
            product.getId().getValue(), product.getName().getValue(), newOrder.getQuantity().getValue(),
            newOrder.getOrderedOn().getValue())));
  }
}
