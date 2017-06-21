package com.example.guest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.guest.dao.OrderGuestView;
import com.example.guest.dao.OrderGuestViewDao;
import com.example.guest.dao.ProductGuestView;
import com.example.guest.dao.ProductGuestViewDao;
import com.example.security.LoginUser;

@RestController
@RequestMapping("guest/api")
public class GuestReadApi {

  private ProductGuestViewDao productGuestViewDao;

  private OrderGuestViewDao orderGuestViewDao;

  @Autowired
  public GuestReadApi(ProductGuestViewDao productGuestViewDao, OrderGuestViewDao orderGuestViewDao) {
    this.productGuestViewDao = productGuestViewDao;
    this.orderGuestViewDao = orderGuestViewDao;
  }

  @GetMapping("products")
  public List<ProductGuestView> getProducts() {
    return productGuestViewDao.selectAll();
  }

  @GetMapping("products/{productId}")
  public ProductGuestView getProduct(@PathVariable("productId") String productId) {
    return productGuestViewDao.selectById(productId);
  }

  @GetMapping("orders")
  public List<OrderGuestView> getOrders(@AuthenticationPrincipal LoginUser loginUser) {
    return orderGuestViewDao.selectByGuestId(loginUser.getId());
  }

}
