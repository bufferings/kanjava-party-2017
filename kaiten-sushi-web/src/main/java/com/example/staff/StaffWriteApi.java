package com.example.staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.order.usecase.OrderUsecase;
import com.example.security.LoginUser;

@RestController
@RequestMapping("staff/api")
public class StaffWriteApi {

  private OrderUsecase orderUsecase;

  @Autowired
  public StaffWriteApi(OrderUsecase orderUsecase) {
    this.orderUsecase = orderUsecase;
  }

  @PostMapping("order-groups/{orderGroupId}/{orderId}/deliver")
  public void deliverOrder(@AuthenticationPrincipal LoginUser loginUser,
      @PathVariable("orderGroupId") String orderGroupId, @PathVariable("orderId") String orderId) {
    orderUsecase.deliverOrder(orderGroupId, orderId, loginUser.getId(), loginUser.getName());
  }

}
