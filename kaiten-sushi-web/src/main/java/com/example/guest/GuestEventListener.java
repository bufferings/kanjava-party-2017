package com.example.guest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.guest.config.GuestKafkaConsumerConfig;
import com.example.guest.dao.GuestOrderView;
import com.example.guest.dao.GuestOrderViewDao;
import com.example.guest.dao.OrderDelivered;
import com.example.order.domain.event.DomainEvent;
import com.example.order.domain.event.OrderCreatedEvent;
import com.example.order.domain.event.OrderDeliveredEvent;
import com.example.order.domain.event.OrderGroupCheckedOutEvent;
import com.example.order.domain.event.StoredEvent;

@Component
public class GuestEventListener {

  private GuestOrderViewDao guestOrderViewDao;

  @Autowired
  public GuestEventListener(GuestOrderViewDao guestOrderViewDao) {
    this.guestOrderViewDao = guestOrderViewDao;
  }

  @KafkaListener(topics = "topic1", containerFactory = GuestKafkaConsumerConfig.CONTAINER_NAME)
  public void listen(StoredEvent storedEvent) {
    DomainEvent event = storedEvent.toDomainEvent();
    if (event instanceof OrderCreatedEvent) {
      handleOrderCreatedEvent((OrderCreatedEvent) event);
    } else if (event instanceof OrderDeliveredEvent) {
      handleOrderDeliveredEvent((OrderDeliveredEvent) event);
    } else if (event instanceof OrderGroupCheckedOutEvent) {
      handleOrderGroupCheckedOutEvent((OrderGroupCheckedOutEvent) event);
    }
  }

  private void handleOrderCreatedEvent(OrderCreatedEvent event) {
    GuestOrderView guestView = createGuestView(event);
    guestOrderViewDao.insert(guestView);
  }

  private GuestOrderView createGuestView(OrderCreatedEvent event) {
    GuestOrderView view = new GuestOrderView();
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
    GuestOrderView guestView = guestOrderViewDao.selectById(event.orderId);
    guestView.deliveryPersonId = event.deliveryPersonId;
    guestView.deliveryPersonName = event.deliveryPersonName;
    guestView.deliveryDateTime = event.deliveryDateTime;
    guestView.delivered = OrderDelivered.DELIVERED;
    guestOrderViewDao.update(guestView);
  }

  private void handleOrderGroupCheckedOutEvent(OrderGroupCheckedOutEvent event) {
    guestOrderViewDao.deleteByGroupId(event.orderGroupId);
  }
}