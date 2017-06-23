package com.example.guest;

import java.time.LocalDateTime;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.guest.dao.GuestOrderItem;
import com.example.guest.dao.GuestOrderItemDao;
import com.example.guest.dao.OrderItemDelivered;

@Component
public class GuestEventListener {

  private static final String ORDER_ITEM_CREATED_EVENT_SCHEMA_NAME = "OrderItemCreatedEvent";

  private static final String ORDER_ITEM_DELIVERED_EVENT_SCHEMA_NAME = "OrderItemDeliveredEvent";

  private static final String ORDER_CHECKED_OUT_EVENT_SCHEMA_NAME = "OrderCheckedOutEvent";

  private GuestOrderItemDao guestOrderItemDao;

  @Autowired
  public GuestEventListener(GuestOrderItemDao guestOrderItemDao) {
    this.guestOrderItemDao = guestOrderItemDao;
  }

  @KafkaListener(topics = "order-topic", containerFactory = GuestKafkaConfig.CONTAINER_NAME)
  public void listen(GenericRecord event) {
    String schemaName = event.getSchema().getFullName();
    if (ORDER_ITEM_CREATED_EVENT_SCHEMA_NAME.equals(schemaName)) {
      handleOrderItemCreatedEvent(event);
    } else if (ORDER_ITEM_DELIVERED_EVENT_SCHEMA_NAME.equals(schemaName)) {
      handleOrderItemDeliveredEvent(event);
    } else if (ORDER_CHECKED_OUT_EVENT_SCHEMA_NAME.equals(schemaName)) {
      handleOrderCheckedOutEvent(event);
    }
  }

  private void handleOrderItemCreatedEvent(GenericRecord event) {
    GuestOrderItem guestView = createGuestOrderItem(event);
    guestOrderItemDao.insert(guestView);
  }

  private GuestOrderItem createGuestOrderItem(GenericRecord event) {
    GuestOrderItem view = new GuestOrderItem();
    view.orderItemId = ((Utf8) event.get("orderItemId")).toString();
    view.orderGuestId = (Integer) event.get("orderGuestId");
    view.orderGuestName = ((Utf8) event.get("orderGuestName")).toString();
    view.productId = ((Utf8) event.get("productId")).toString();
    view.quantity = (Integer) event.get("quantity");
    view.orderedOn = LocalDateTime.parse(((Utf8) event.get("orderedOn")).toString());
    view.delivered = OrderItemDelivered.NOT_DELIVERED;
    return view;
  }

  private void handleOrderItemDeliveredEvent(GenericRecord event) {
    String orderItemId = ((Utf8) event.get("orderItemId")).toString();
    GuestOrderItem guestView = guestOrderItemDao.selectByOrderItemId(orderItemId);
    guestView.delivered = OrderItemDelivered.DELIVERED;
    guestView.deliveryPersonId = (Integer) event.get("deliveryPersonId");
    guestView.deliveryPersonName = ((Utf8) event.get("deliveryPersonName")).toString();
    guestView.deliveredOn = LocalDateTime.parse(((Utf8) event.get("deliveredOn")).toString());
    guestOrderItemDao.update(guestView);
  }

  private void handleOrderCheckedOutEvent(GenericRecord event) {
    guestOrderItemDao.deleteByOrderGuestId((Integer) event.get("orderGuestId"));
  }
}