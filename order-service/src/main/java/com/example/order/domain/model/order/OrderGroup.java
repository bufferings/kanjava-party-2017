package com.example.order.domain.model.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import com.example.order.domain.model.product.ProductId;
import com.example.order.util.IdUtil;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class OrderGroup {

  public static OrderGroup newOrderGroup(OrderGroupId id, OrderGuestId orderGuestId, OrderGuestName orderGuestName) {
    return new OrderGroup(id, orderGuestId, orderGuestName, OrderGroupStatus.OPENED, new ArrayList<OrderItem>(), -1);
  }

  public static OrderGroup restoreFromDataStore(OrderGroupId id, OrderGuestId orderGuestId, OrderGuestName orderGuestName,
      OrderGroupStatus status, List<OrderItem> orderItems, Integer version) {
    Assert.notNull(version);
    Assert.isTrue(version >= 1);
    return new OrderGroup(id, orderGuestId, orderGuestName, status, orderItems, version);
  }

  private OrderGroupId id;

  private OrderGuestId orderGuestId;

  private OrderGuestName orderGuestName;

  private OrderGroupStatus status;

  private List<OrderItem> orderItems;

  private Integer version;

  private OrderGroup(OrderGroupId id, OrderGuestId orderGuestId, OrderGuestName orderGuestName, OrderGroupStatus status,
      List<OrderItem> orderItems, Integer version) {
    this.setId(id);
    this.setOrderGuestId(orderGuestId);
    this.setOrderGuestName(orderGuestName);
    this.setStatus(status);
    this.setOrderItems(orderItems);
    this.setVersion(version);
  }

  public OrderItem addOrderItem(ProductId productId, OrderQuantity quantity) {
    OrderItem newOrderItem = OrderItem.newOrderItem(new OrderItemId(IdUtil.generateId()), productId, quantity);
    this.getOrderItems().add(newOrderItem);
    return newOrderItem;
  }

  public boolean canCheckout() {
    return this.getStatus() == OrderGroupStatus.OPENED;
  }

  public void checkout() {
    Assert.state(canCheckout());
    this.setStatus(OrderGroupStatus.CLOSED);
  }

  public boolean canDeliverOrderItem(OrderItemId orderItemId) {
    if (this.getStatus() == OrderGroupStatus.CLOSED) {
      return false;
    }
    OrderItem orderItem = getOrderItem(orderItemId);
    if (orderItem == null) {
      return false;
    }
    return orderItem.canDeliver();
  }

  public void deliverOrderItem(OrderItemId orderItemId, DeliveryPersonId deliveryPersonId, DeliveryPersonName deliveryPersonName,
      DeliveryDateTime deliveredOn) {
    Assert.state(canDeliverOrderItem(orderItemId));
    getOrderItem(orderItemId).deliver(deliveryPersonId, deliveryPersonName, deliveredOn);
  }

  private OrderItem getOrderItem(OrderItemId orderItemId) {
    for (OrderItem orderItem : orderItems) {
      if (orderItem.getId().equals(orderItemId)) {
        return orderItem;
      }
    }
    return null;
  }

  public boolean isNew() {
    return this.getVersion() == -1;
  }

  public void incrementVersion() {
    if (isNew()) {
      this.setVersion(1);
    } else {
      this.setVersion(this.getVersion().intValue() + 1);
    }
  }

  private void setId(OrderGroupId id) {
    Assert.notNull(id);
    this.id = id;
  }

  private void setOrderGuestId(OrderGuestId orderGuestId) {
    Assert.notNull(orderGuestId);
    this.orderGuestId = orderGuestId;
  }

  private void setOrderGuestName(OrderGuestName orderGuestName) {
    Assert.notNull(orderGuestName);
    this.orderGuestName = orderGuestName;
  }

  private void setStatus(OrderGroupStatus status) {
    Assert.notNull(status);
    this.status = status;
  }

  private void setOrderItems(List<OrderItem> orderItems) {
    Assert.notNull(orderItems);
    this.orderItems = orderItems;
  }

  private void setVersion(Integer version) {
    Assert.notNull(version);
    this.version = version;
  }

}
