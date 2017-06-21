package com.example.guest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.guest.config.GuestKafkaConsumerConfig;
import com.example.guest.dao.GuestOrderItem;
import com.example.guest.dao.GuestOrderItemDao;
import com.example.guest.dao.OrderItemDelivered;
import com.example.order.domain.event.DomainEvent;
import com.example.order.domain.event.OrderCheckedOutEvent;
import com.example.order.domain.event.OrderItemCreatedEvent;
import com.example.order.domain.event.OrderItemDeliveredEvent;
import com.example.order.domain.event.StoredEvent;

@Component
public class GuestEventListener {

  private GuestOrderItemDao guestOrderItemDao;

  @Autowired
  public GuestEventListener(GuestOrderItemDao guestOrderItemDao) {
    this.guestOrderItemDao = guestOrderItemDao;
  }

  @KafkaListener(topics = "topic1", containerFactory = GuestKafkaConsumerConfig.CONTAINER_NAME)
  public void listen(StoredEvent storedEvent) {
    DomainEvent event = storedEvent.toDomainEvent();
    if (event instanceof OrderItemCreatedEvent) {
      handleOrderItemCreatedEvent((OrderItemCreatedEvent) event);
    } else if (event instanceof OrderItemDeliveredEvent) {
      handleOrderItemDeliveredEvent((OrderItemDeliveredEvent) event);
    } else if (event instanceof OrderCheckedOutEvent) {
      handleOrderCheckedOutEvent((OrderCheckedOutEvent) event);
    }
  }

  private void handleOrderItemCreatedEvent(OrderItemCreatedEvent event) {
    GuestOrderItem guestView = createGuestOrderItem(event);
    guestOrderItemDao.insert(guestView);
  }

  private GuestOrderItem createGuestOrderItem(OrderItemCreatedEvent event) {
    GuestOrderItem view = new GuestOrderItem();
    view.orderItemId = event.orderItemId;
    view.orderGuestId = event.orderGuestId;
    view.orderGuestName = event.orderGuestName;
    view.productId = event.productId;
    view.productName = event.productName;
    view.quantity = event.quantity;
    view.orderedOn = event.orderedOn;
    view.delivered = OrderItemDelivered.NOT_DELIVERED;
    return view;
  }

  private void handleOrderItemDeliveredEvent(OrderItemDeliveredEvent event) {
    GuestOrderItem guestView = guestOrderItemDao.selectByOrderItemId(event.orderItemId);
    guestView.delivered = OrderItemDelivered.DELIVERED;
    guestView.deliveryPersonId = event.deliveryPersonId;
    guestView.deliveryPersonName = event.deliveryPersonName;
    guestView.deliveredOn = event.deliveredOn;
    guestOrderItemDao.update(guestView);
  }

  private void handleOrderCheckedOutEvent(OrderCheckedOutEvent event) {
    guestOrderItemDao.deleteByOrderGuestId(event.orderGuestId);
  }
}