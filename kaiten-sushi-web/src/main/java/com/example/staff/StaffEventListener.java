package com.example.staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.order.domain.event.DomainEvent;
import com.example.order.domain.event.OrderCreatedEvent;
import com.example.order.domain.event.OrderDeliveredEvent;
import com.example.order.domain.event.OrderGroupClosedEvent;
import com.example.order.domain.event.StoredEvent;
import com.example.staff.config.StaffKafkaConsumerConfig;
import com.example.staff.dao.OrderStaffView;
import com.example.staff.dao.OrderStaffViewDao;

@Component
public class StaffEventListener {

  private OrderStaffViewDao orderStaffViewDao;

  @Autowired
  public StaffEventListener(OrderStaffViewDao orderStaffViewDao) {
    this.orderStaffViewDao = orderStaffViewDao;
  }

  @KafkaListener(topics = "topic1", containerFactory = StaffKafkaConsumerConfig.CONTAINER_NAME)
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

  private void handleOrderDeliveredEvent(OrderDeliveredEvent event) {
    OrderStaffView staffView = orderStaffViewDao.selectById(event.orderId);
    orderStaffViewDao.delete(staffView);
  }

  private void handleOrderGroupClosedEvent(OrderGroupClosedEvent event) {
    orderStaffViewDao.deleteByGroupId(event.orderGroupId);
  }
}