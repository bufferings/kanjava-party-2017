package com.example.staff;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.staff.dao.StaffOrderView;
import com.example.staff.dao.StaffOrderViewDao;

@RestController
@RequestMapping("staff/api")
public class StaffReadApi {

  private StaffOrderViewDao orderStaffViewDao;

  @Autowired
  public StaffReadApi(StaffOrderViewDao orderStaffViewDao) {
    this.orderStaffViewDao = orderStaffViewDao;
  }

  @GetMapping("orders/waiting")
  public List<StaffOrderView> getOrders() {
    return orderStaffViewDao.selectForStaff();
  }

}
