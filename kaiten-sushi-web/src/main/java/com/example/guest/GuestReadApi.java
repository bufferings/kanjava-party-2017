package com.example.guest;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.guest.dao.order.OrderGuestView;
import com.example.guest.dao.order.OrderGuestViewDao;
import com.example.guest.dao.product.ProductGuestView;
import com.example.guest.dao.product.ProductGuestViewDao;

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
  public List<OrderGuestView> getOrders(Principal principal) {
    System.err.println(principal);
    return orderGuestViewDao.selectByTableNumber(21);
  }

}
