package com.example.guest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.guest.config.GuestKafkaConsumerConfig;
import com.example.guest.dao.OrderDelivered;
import com.example.guest.dao.OrderGuestView;
import com.example.guest.dao.OrderGuestViewDao;
import com.example.order.domain.event.DomainEvent;
import com.example.order.domain.event.OrderCreatedEvent;
import com.example.order.domain.event.OrderDeliveredEvent;
import com.example.order.domain.event.OrderGroupClosedEvent;
import com.example.order.domain.event.StoredEvent;

@Component
public class GuestEventListener {

  private OrderGuestViewDao orderGuestViewDao;

  @Autowired
  public GuestEventListener(OrderGuestViewDao orderGuestViewDao) {
    this.orderGuestViewDao = orderGuestViewDao;
  }

  @KafkaListener(topics = "topic1", containerFactory = GuestKafkaConsumerConfig.CONTAINER_NAME)
  public void listen(StoredEvent storedEvent) {
    DomainEvent event = storedEvent.toDomainEvent();
    if (event instanceof OrderCreatedEvent) {
      handleOrderCreatedEvent((OrderCreatedEvent) event);
    } else if (event instanceof OrderDeliveredEvent) {
      handleOrderDeliveredEvent((OrderDeliveredEvent) event);
    } else if (event instanceof OrderGroupClosedEvent) {
      handleOrderGroupClosedEvent((OrderGroupClosedEvent) event);
    }
  }

  private void handleOrderCreatedEvent(OrderCreatedEvent event) {
    OrderGuestView guestView = createGuestView(event);
    orderGuestViewDao.insert(guestView);
  }

  private OrderGuestView createGuestView(OrderCreatedEvent event) {
    OrderGuestView view = new OrderGuestView();
    view.orderId = event.orderId;
    view.orderGroupId = event.orderGroupId;
    view.orderGuestId = event.orderGuestId;
    view.orderGuestName = event.orderGuestName;
    view.productId = event.productId;
    view.productName = event.productName;
    view.quantity = event.quantity;
    view.orderDateTime = event.orderDateTime;
    view.delivered = OrderDelivered.NOT_DELIVERED;
    return view;
  }

  private void handleOrderDeliveredEvent(OrderDeliveredEvent event) {
    OrderGuestView guestView = orderGuestViewDao.selectById(event.orderId);
    guestView.delivered = OrderDelivered.DELIVERED;
    orderGuestViewDao.update(guestView);
  }

  private void handleOrderGroupClosedEvent(OrderGroupClosedEvent event) {
    orderGuestViewDao.deleteByGroupId(event.orderGroupId);
  }
}