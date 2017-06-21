package com.example.staff;

import java.time.LocalDateTime;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.staff.config.StaffKafkaConsumerConfig;
import com.example.staff.dao.StaffOrderItem;
import com.example.staff.dao.StaffOrderItemDao;

@Component
public class StaffEventListener {

  private static final String ORDER_ITEM_CREATED_EVENT_SCHEMA_NAME = "OrderItemCreatedEvent";

  private static final String ORDER_ITEM_DELIVERED_EVENT_SCHEMA_NAME = "OrderItemDeliveredEvent";

  private static final String ORDER_CHECKED_OUT_EVENT_SCHEMA_NAME = "OrderCheckedOutEvent";

  private StaffOrderItemDao staffOrderItemDao;

  @Autowired
  public StaffEventListener(StaffOrderItemDao staffOrderItemDao) {
    this.staffOrderItemDao = staffOrderItemDao;
  }

  @KafkaListener(topics = "order-topic", containerFactory = StaffKafkaConsumerConfig.CONTAINER_NAME)
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
    StaffOrderItem staffView = createStaffOrderItem(event);
    staffOrderItemDao.insert(staffView);
  }

  private StaffOrderItem createStaffOrderItem(GenericRecord event) {
    StaffOrderItem view = new StaffOrderItem();
    view.orderItemId = ((Utf8) event.get("orderItemId")).toString();
    view.orderGuestId = (Integer) event.get("orderGuestId");
    view.orderGuestName = ((Utf8) event.get("orderGuestName")).toString();
    view.productId = ((Utf8) event.get("productId")).toString();
    view.productName = ((Utf8) event.get("productName")).toString();
    view.quantity = (Integer) event.get("quantity");
    view.orderedOn = LocalDateTime.parse(((Utf8) event.get("orderedOn")).toString());
    return view;
  }

  private void handleOrderItemDeliveredEvent(GenericRecord event) {
    staffOrderItemDao.deleteByOrderItemId(((Utf8) event.get("orderItemId")).toString());
  }

  private void handleOrderCheckedOutEvent(GenericRecord event) {
    staffOrderItemDao.deleteByOrderGuestId((Integer) event.get("orderGuestId"));
  }
}