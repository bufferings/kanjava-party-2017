package com.example.guest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.order.usecase.OrderUsecase;
import com.example.security.LoginUser;

@RestController
@RequestMapping("guest/api")
public class GuestApi {

  private OrderUsecase orderUsecase;

  @Autowired
  public GuestApi(OrderUsecase orderUsecase) {
    this.orderUsecase = orderUsecase;
  }

  @RequestMapping(path = "orders/add", method = RequestMethod.POST)
  public void addOrder(@AuthenticationPrincipal LoginUser loginUser, @RequestParam("productId") String productId,
      @RequestParam("quantity") int quantity) {
    orderUsecase.addOrder(loginUser.getId(), loginUser.getName(), productId, quantity);
  }

  @RequestMapping(path = "checkout", method = RequestMethod.POST)
  public void checkout(@AuthenticationPrincipal LoginUser loginUser) {
    orderUsecase.checkout(loginUser.getId());
  }

}
