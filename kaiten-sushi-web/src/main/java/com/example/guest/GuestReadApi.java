package com.example.guest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.guest.dao.GuestOrderView;
import com.example.guest.dao.GuestOrderViewDao;
import com.example.guest.dao.GuestProductView;
import com.example.guest.dao.GuestProductViewDao;
import com.example.security.LoginUser;

@RestController
@RequestMapping("guest/api")
public class GuestReadApi {

  private GuestProductViewDao guestProductViewDao;

  private GuestOrderViewDao guestOrderViewDao;

  @Autowired
  public GuestReadApi(GuestProductViewDao guestProductViewDao, GuestOrderViewDao guestOrderViewDao) {
    this.guestProductViewDao = guestProductViewDao;
    this.guestOrderViewDao = guestOrderViewDao;
  }

  @GetMapping("products")
  public List<GuestProductView> products() {
    return guestProductViewDao.selectAll();
  }

  @GetMapping("products/{productId}")
  public GuestProductView product(@PathVariable("productId") String productId) {
    return guestProductViewDao.selectById(productId);
  }

  @GetMapping("orders")
  public List<GuestOrderView> orders(@AuthenticationPrincipal LoginUser loginUser) {
    return guestOrderViewDao.selectByGuestId(loginUser.getId());
  }

}
