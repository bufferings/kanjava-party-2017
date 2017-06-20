package com.example.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.api.view.OrderStaffView;
import com.example.api.view.OrderStaffViewDao;
import com.example.guest.dao.order.OrderDelivered;
import com.example.guest.dao.order.OrderGuestView;
import com.example.guest.dao.order.OrderGuestViewDao;
import com.example.order.domain.event.DomainEvent;
import com.example.order.domain.event.OrderCreatedEvent;
import com.example.order.domain.event.OrderDeliveredEvent;
import com.example.order.domain.event.OrderGroupClosedEvent;
import com.example.order.domain.event.StoredEvent;

@Component
public class Listener {

  private OrderGuestViewDao orderGuestViewDao;

  private OrderStaffViewDao orderStaffViewDao;

  @Autowired
  public Listener(OrderGuestViewDao orderGuestViewDao, OrderStaffViewDao orderStaffViewDao) {
    this.orderGuestViewDao = orderGuestViewDao;
    this.orderStaffViewDao = orderStaffViewDao;
  }

  @KafkaListener(id = "foo", topics = "topic1", group = "group1")
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

    OrderStaffView staffView = createStaffView(event);
    orderStaffViewDao.insert(staffView);
  }

  private OrderStaffView createStaffView(OrderCreatedEvent event) {
    OrderStaffView view = new OrderStaffView();
    view.orderId = event.orderId;
    view.orderGroupId = event.orderGroupId;
    view.orderGuestId = event.orderGuestId;
    view.orderGuestName = event.orderGuestName;
    view.productId = event.productId;
    view.productName = event.productName;
    view.quantity = event.quantity;
    view.orderDateTime = event.orderDateTime;
    return view;
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

    OrderStaffView staffView = orderStaffViewDao.selectById(event.orderId);
    orderStaffViewDao.delete(staffView);
  }

  private void handleOrderGroupClosedEvent(OrderGroupClosedEvent event) {
    orderGuestViewDao.deleteByGroupId(event.orderGroupId);
    orderStaffViewDao.deleteByGroupId(event.orderGroupId);
  }
}