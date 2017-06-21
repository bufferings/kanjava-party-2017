package com.example.guest.dao;

import java.time.LocalDateTime;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;

import lombok.ToString;

@Entity
@ToString
public class OrderGuestView {

  @Id
  public String orderId;

  public String orderGroupId;

  public Integer orderGuestId;

  public String orderGuestName;

  public String productId;

  public String productName;

  public Integer quantity;

  public LocalDateTime orderDateTime;

  public OrderDelivered delivered;

}
