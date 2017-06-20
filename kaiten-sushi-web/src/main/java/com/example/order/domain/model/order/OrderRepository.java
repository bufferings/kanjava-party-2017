package com.example.order.domain.model.order;

import java.util.List;

public interface OrderRepository {

  void save(OrderGroup orderGroup);

  OrderGroup activeOrderGroupOf(OrderGuestId guestId);

  OrderGroup orderGroupOfId(OrderGroupId orderGroupId);

  List<OrderGroup> checkoutOrderGroups();

}
