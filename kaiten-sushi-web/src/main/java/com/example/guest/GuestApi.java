package com.example.guest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.order.usecase.OrderUsecase;

@RestController
@RequestMapping("guest/api")
public class GuestApi {

  private static final int DUMMY_TABLE_NUMBER = 21;

  private OrderUsecase orderUsecase;

  @Autowired
  public GuestApi(OrderUsecase orderUsecase) {
    this.orderUsecase = orderUsecase;
  }

  @RequestMapping(path = "orders/add", method = RequestMethod.POST)
  public void addOrder(@RequestParam("productId") String productId, @RequestParam("quantity") int quantity) {
    int tableNumber = DUMMY_TABLE_NUMBER;
    orderUsecase.addOrder(tableNumber, productId, quantity);
  }

  @RequestMapping(path = "checkout", method = RequestMethod.POST)
  public void checkout() {
    int tableNumber = DUMMY_TABLE_NUMBER;
    orderUsecase.checkout(tableNumber);
  }

}
