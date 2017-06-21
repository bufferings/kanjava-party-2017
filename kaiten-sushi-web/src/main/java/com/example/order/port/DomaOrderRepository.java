package com.example.order.port;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.order.domain.model.order.DeliveryDateTime;
import com.example.order.domain.model.order.DeliveryPersonId;
import com.example.order.domain.model.order.DeliveryPersonName;
import com.example.order.domain.model.order.OrderDateTime;
import com.example.order.domain.model.order.OrderGroup;
import com.example.order.domain.model.order.OrderGroupId;
import com.example.order.domain.model.order.OrderGroupStatus;
import com.example.order.domain.model.order.OrderGuestId;
import com.example.order.domain.model.order.OrderGuestName;
import com.example.order.domain.model.order.OrderItem;
import com.example.order.domain.model.order.OrderItemId;
import com.example.order.domain.model.order.OrderQuantity;
import com.example.order.domain.model.order.OrderRepository;
import com.example.order.domain.model.order.OrderStatus;
import com.example.order.domain.model.product.ProductId;
import com.example.order.port.table.ordergroup.OrderGroupTable;
import com.example.order.port.table.ordergroup.OrderGroupTableDao;
import com.example.order.port.table.orderitem.OrderItemTable;
import com.example.order.port.table.orderitem.OrderItemTableDao;

@Repository
public class DomaOrderRepository implements OrderRepository {

  private OrderGroupTableDao orderGroupTableDao;

  private OrderItemTableDao orderItemTableDao;

  @Autowired
  public DomaOrderRepository(OrderGroupTableDao orderGroupTableDao, OrderItemTableDao orderItemTableDao) {
    this.orderGroupTableDao = orderGroupTableDao;
    this.orderItemTableDao = orderItemTableDao;
  }

  @Override
  public void save(OrderGroup orderGroup) {
    OrderGroupTable orderGroupRecord = orderGroupRecordFrom(orderGroup);
    List<OrderItemTable> orderItemRecords;
    if (orderGroup.isNew()) {
      orderGroupTableDao.insert(orderGroupRecord);
      orderItemRecords = new ArrayList<>();
    } else {
      orderGroupTableDao.update(orderGroupRecord);
      orderItemRecords = orderItemTableDao.selectByOrderGroupid(orderGroup.getId().getValue());
    }

    for (OrderItem orderItem : orderGroup.getOrderItems()) {
      OrderItemTable orderItemRecord = orderItemRecordFrom(orderGroup.getId(), orderItem);
      if (exists(orderItemRecord, orderItemRecords)) {
        orderItemTableDao.update(orderItemRecord);
      } else {
        orderItemTableDao.insert(orderItemRecord);
      }
    }

    orderGroup.incrementVersion();
  }

  @Override
  public OrderGroup activeOrderGroupOf(OrderGuestId guestId) {
    OrderGroupTable orderGroupRecord = orderGroupTableDao.selectCurrentForGuest(guestId.getValue());
    if (orderGroupRecord == null) {
      return null;
    }
    List<OrderItemTable> orderRecords = orderItemTableDao.selectByOrderGroupid(orderGroupRecord.orderGroupId);
    return orderGroupFrom(orderGroupRecord, orderRecords);
  }

  @Override
  public OrderGroup orderGroupOfOrderItemId(OrderItemId orderItemId) {
    OrderItemTable orderItemTable = orderItemTableDao.selectById(orderItemId.getValue());
    if (orderItemTable == null) {
      return null;
    }

    OrderGroupTable orderGroupRecord = orderGroupTableDao.selectById(orderItemTable.orderGroupId);
    if (orderGroupRecord == null) {
      return null;
    }

    List<OrderItemTable> orderRecords = orderItemTableDao.selectByOrderGroupid(orderGroupRecord.orderGroupId);
    return orderGroupFrom(orderGroupRecord, orderRecords);
  }

  private OrderGroup orderGroupFrom(OrderGroupTable orderGroupRecord, List<OrderItemTable> orderRecords) {
    List<OrderItem> orders = new ArrayList<>();
    for (OrderItemTable orderRecord : orderRecords) {
      orders.add(orderFrom(orderRecord));
    }
    return OrderGroup.restoreFromDataStore(new OrderGroupId(orderGroupRecord.orderGroupId),
        new OrderGuestId(orderGroupRecord.orderGuestId), new OrderGuestName(orderGroupRecord.orderGuestName),
        orderGroupStatusFrom(orderGroupRecord.status), orders, orderGroupRecord.version);
  }

  private OrderGroupStatus orderGroupStatusFrom(Integer status) {
    if (status == 1) {
      return OrderGroupStatus.OPENED;
    } else if (status == 2) {
      return OrderGroupStatus.CLOSED;
    } else {
      throw new RuntimeException("Unknown status.");
    }
  }

  private OrderItem orderFrom(OrderItemTable orderRecord) {
    return OrderItem.restoreFromDataStore(new OrderItemId(orderRecord.orderItemId),
        new ProductId(orderRecord.productId), new OrderQuantity(orderRecord.quantity),
        new OrderDateTime(orderRecord.orderDateTime),
        (orderRecord.deliveryPersonId != null ? new DeliveryPersonId(orderRecord.deliveryPersonId) : null),
        (orderRecord.deliveryPersonName != null ? new DeliveryPersonName(orderRecord.deliveryPersonName) : null),
        (orderRecord.deliveryDateTime != null ? new DeliveryDateTime(orderRecord.deliveryDateTime) : null),
        orderStatusFrom(orderRecord.status));
  }

  private OrderStatus orderStatusFrom(Integer status) {
    if (status == 1) {
      return OrderStatus.ORDERED;
    } else if (status == 2) {
      return OrderStatus.DELIVERED;
    } else {
      throw new RuntimeException("Unknown status.");
    }
  }

  private OrderGroupTable orderGroupRecordFrom(OrderGroup orderGroup) {
    OrderGroupTable record = new OrderGroupTable();
    record.orderGroupId = orderGroup.getId().getValue();
    record.orderGuestId = orderGroup.getOrderGuestId().getValue();
    record.orderGuestName = orderGroup.getOrderGuestName().getValue();
    record.status = orderGroupRecordStatusFrom(orderGroup.getStatus());
    record.version = orderGroup.getVersion();
    return record;
  }

  private Integer orderGroupRecordStatusFrom(OrderGroupStatus status) {
    switch (status) {
    case OPENED:
      return 1;
    case CLOSED:
      return 2;
    default:
      throw new RuntimeException("Unknown status.");
    }
  }

  private OrderItemTable orderItemRecordFrom(OrderGroupId orderGroupId, OrderItem order) {
    OrderItemTable orderItemRecord = new OrderItemTable();
    orderItemRecord.orderItemId = order.getId().getValue();
    orderItemRecord.orderGroupId = orderGroupId.getValue();
    orderItemRecord.productId = order.getProductId().getValue();
    orderItemRecord.orderDateTime = order.getOrderedOn().getValue();
    orderItemRecord.quantity = order.getQuantity().getValue();
    orderItemRecord.deliveryPersonId = (order.getDeliveryPersonId() != null ? order.getDeliveryPersonId().getValue()
        : null);
    orderItemRecord.deliveryPersonName = (order.getDeliveryPersonName() != null
        ? order.getDeliveryPersonName().getValue() : null);
    orderItemRecord.deliveryDateTime = (order.getDeliveredOn() != null ? order.getDeliveredOn().getValue() : null);
    orderItemRecord.status = orderItemRecordStatusFrom(order.getStatus());
    return orderItemRecord;
  }

  private Integer orderItemRecordStatusFrom(OrderStatus status) {
    switch (status) {
    case ORDERED:
      return 1;
    case DELIVERED:
      return 2;
    default:
      throw new RuntimeException("Unknown status.");
    }
  }

  private boolean exists(OrderItemTable target, List<OrderItemTable> existingOrderItemRecords) {
    for (OrderItemTable record : existingOrderItemRecords) {
      if (record.orderItemId.equals(target.orderItemId)) {
        return true;
      }
    }
    return false;
  }

}
