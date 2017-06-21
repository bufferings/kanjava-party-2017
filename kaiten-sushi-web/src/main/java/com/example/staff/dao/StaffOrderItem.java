package com.example.staff.dao;

import java.time.LocalDateTime;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;

import lombok.ToString;

@Entity
@ToString
public class StaffOrderItem {

  @Id
  public String orderItemId;

  public Integer orderGuestId;

  public String orderGuestName;

  public String productId;

  public String productName;

  public Integer quantity;

  public LocalDateTime orderedOn;

}
