package com.example.staff;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.staff.dao.StaffOrderItem;
import com.example.staff.dao.StaffOrderItemDao;

@RestController
@RequestMapping("staff/api")
public class StaffReadApi {

  private StaffOrderItemDao orderStaffItemDao;

  @Autowired
  public StaffReadApi(StaffOrderItemDao orderStaffItemDao) {
    this.orderStaffItemDao = orderStaffItemDao;
  }

  @GetMapping("order-items")
  public List<StaffOrderItem> orderItems() {
    return orderStaffItemDao.selectForStaff();
  }

}
