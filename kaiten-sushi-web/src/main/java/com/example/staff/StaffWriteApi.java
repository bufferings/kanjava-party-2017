package com.example.staff;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.order.domain.model.order.OrderGroup;
import com.example.order.usecase.OrderUsecase;

@RestController
@RequestMapping("staff/api")
public class StaffWriteApi {

  private OrderUsecase orderUsecase;

  @Autowired
  public StaffWriteApi(OrderUsecase orderUsecase) {
    this.orderUsecase = orderUsecase;
  }

  @PostMapping("order-groups/{orderGroupId}/{orderId}/deliver")
  public void deliverOrder(@PathVariable("orderGroupId") String orderGroupId, @PathVariable("orderId") String orderId) {
    orderUsecase.deliver(orderGroupId, orderId);
  }

  @PostMapping("order-groups/{orderGroupId}/close")
  public void closeOrderGroup(@PathVariable("orderGroupId") String orderGroupId) {
    orderUsecase.close(orderGroupId);
  }

  // TODO Read側に移したい(Read側のストアが必要)
  @GetMapping("order-groups/checkout")
  public List<OrderGroup> getCheckoutOrderGroups() {
    return orderUsecase.checkoutOrderGroups();
  }
}
