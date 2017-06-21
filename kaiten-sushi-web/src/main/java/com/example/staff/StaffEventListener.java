package com.example.staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.order.domain.event.DomainEvent;
import com.example.order.domain.event.OrderCheckedOutEvent;
import com.example.order.domain.event.OrderItemCreatedEvent;
import com.example.order.domain.event.OrderItemDeliveredEvent;
import com.example.order.domain.event.StoredEvent;
import com.example.staff.config.StaffKafkaConsumerConfig;
import com.example.staff.dao.StaffOrderItem;
import com.example.staff.dao.StaffOrderItemDao;

@Component
public class StaffEventListener {

  private StaffOrderItemDao staffOrderItemDao;

  @Autowired
  public StaffEventListener(StaffOrderItemDao staffOrderItemDao) {
    this.staffOrderItemDao = staffOrderItemDao;
  }

  @KafkaListener(topics = "topic1", containerFactory = StaffKafkaConsumerConfig.CONTAINER_NAME)
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
    StaffOrderItem staffView = createStaffOrderItem(event);
    staffOrderItemDao.insert(staffView);
  }

  private StaffOrderItem createStaffOrderItem(OrderItemCreatedEvent event) {
    StaffOrderItem view = new StaffOrderItem();
    view.orderItemId = event.orderItemId;
    view.orderGuestId = event.orderGuestId;
    view.orderGuestName = event.orderGuestName;
    view.productId = event.productId;
    view.productName = event.productName;
    view.quantity = event.quantity;
    view.orderedOn = event.orderedOn;
    return view;
  }

  private void handleOrderItemDeliveredEvent(OrderItemDeliveredEvent event) {
    staffOrderItemDao.deleteByOrderItemId(event.orderItemId);
  }

  private void handleOrderCheckedOutEvent(OrderCheckedOutEvent event) {
    staffOrderItemDao.deleteByOrderGuestId(event.orderGuestId);
  }
}