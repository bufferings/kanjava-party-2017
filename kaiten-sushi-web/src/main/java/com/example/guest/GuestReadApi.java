package com.example.guest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.guest.dao.GuestOrderItem;
import com.example.guest.dao.GuestOrderItemDao;
import com.example.guest.dao.GuestProduct;
import com.example.guest.dao.GuestProductDao;
import com.example.security.LoginUser;

@RestController
@RequestMapping("guest/api")
public class GuestReadApi {

  private GuestProductDao guestProductDao;

  private GuestOrderItemDao guestOrderItemDao;

  @Autowired
  public GuestReadApi(GuestProductDao guestProductDao, GuestOrderItemDao guestOrderItemDao) {
    this.guestProductDao = guestProductDao;
    this.guestOrderItemDao = guestOrderItemDao;
  }

  @GetMapping("products")
  public List<GuestProduct> products() {
    return guestProductDao.selectAll();
  }

  @GetMapping("products/{productId}")
  public GuestProduct product(@PathVariable("productId") String productId) {
    return guestProductDao.selectByProductId(productId);
  }

  @GetMapping("order-items")
  public List<GuestOrderItem> orderItems(@AuthenticationPrincipal LoginUser loginUser) {
    return guestOrderItemDao.selectByOrderGuestId(loginUser.getId());
  }

}
