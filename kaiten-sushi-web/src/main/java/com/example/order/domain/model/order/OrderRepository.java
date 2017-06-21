package com.example.order.domain.model.order;

public interface OrderRepository {

  void save(OrderGroup orderGroup);

  OrderGroup activeOrderGroupOf(OrderGuestId guestId);

  OrderGroup orderGroupOfOrderItemId(OrderItemId orderItemId);

}
