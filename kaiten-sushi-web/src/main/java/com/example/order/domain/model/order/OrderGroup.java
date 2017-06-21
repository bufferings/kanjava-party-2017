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

  public static OrderGroup newOrderGroup(OrderGroupId id, OrderGuestId guestId, OrderGuestName guestName) {
    return new OrderGroup(id, guestId, guestName, OrderGroupStatus.OPENED, new ArrayList<Order>(), -1);
  }

  public static OrderGroup restoreFromDataStore(OrderGroupId id, OrderGuestId guestId, OrderGuestName guestName,
      OrderGroupStatus status, List<Order> orders, Integer version) {
    Assert.notNull(version);
    Assert.isTrue(version >= 1);
    return new OrderGroup(id, guestId, guestName, status, orders, version);
  }

  private OrderGroupId id;

  private OrderGuestId guestId;

  private OrderGuestName guestName;

  private OrderGroupStatus status;

  private List<Order> orders;

  private Integer version;

  private OrderGroup(OrderGroupId id, OrderGuestId guestId, OrderGuestName guestName, OrderGroupStatus status,
      List<Order> orders, Integer version) {
    this.setId(id);
    this.setGuestId(guestId);
    this.setGuestName(guestName);
    this.setStatus(status);
    this.setOrders(orders);
    this.setVersion(version);
  }

  public Order addOrder(ProductId productId, OrderQuantity quantity) {
    Order newOrder = Order.newOrder(new OrderId(IdUtil.generateId()), productId, quantity);
    this.getOrders().add(newOrder);
    return newOrder;
  }

  public boolean canCheckout() {
    return this.getStatus() == OrderGroupStatus.OPENED;
  }

  public void checkout() {
    Assert.state(canCheckout());
    this.setStatus(OrderGroupStatus.CLOSED);
  }

  public boolean canDeliverOrder(OrderId orderId) {
    if (this.getStatus() == OrderGroupStatus.CLOSED) {
      return false;
    }
    Order order = getOrder(orderId);
    if (order == null) {
      return false;
    }
    return order.canDeliver();
  }

  public void deliverOrder(OrderId orderId, DeliveryPersonId deliveryPersonId, DeliveryPersonName deliveryPersonName,
      DeliveryDateTime deliveredOn) {
    Assert.state(canDeliverOrder(orderId));
    getOrder(orderId).deliver(deliveryPersonId, deliveryPersonName, deliveredOn);
  }

  private Order getOrder(OrderId orderId) {
    for (Order order : orders) {
      if (order.getId().equals(orderId)) {
        return order;
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

  private void setGuestId(OrderGuestId guestId) {
    Assert.notNull(guestId);
    this.guestId = guestId;
  }

  private void setGuestName(OrderGuestName guestName) {
    Assert.notNull(guestName);
    this.guestName = guestName;
  }

  private void setStatus(OrderGroupStatus status) {
    Assert.notNull(status);
    this.status = status;
  }

  private void setOrders(List<Order> orders) {
    Assert.notNull(orders);
    this.orders = orders;
  }

  private void setVersion(Integer version) {
    Assert.notNull(version);
    this.version = version;
  }

}
