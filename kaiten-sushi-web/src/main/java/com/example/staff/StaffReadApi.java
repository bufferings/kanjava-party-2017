package com.example.staff;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.staff.dao.OrderStaffView;
import com.example.staff.dao.OrderStaffViewDao;

@RestController
@RequestMapping("staff/api")
public class StaffReadApi {

  private OrderStaffViewDao orderStaffViewDao;

  @Autowired
  public StaffReadApi(OrderStaffViewDao orderStaffViewDao) {
    this.orderStaffViewDao = orderStaffViewDao;
  }

  @GetMapping("orders/waiting")
  public List<OrderStaffView> getOrders() {
    return orderStaffViewDao.selectForStaff();
  }

}
