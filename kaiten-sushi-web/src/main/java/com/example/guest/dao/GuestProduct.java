package com.example.guest.dao;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;

import lombok.ToString;

@Entity
@ToString
public class GuestProduct {

  @Id
  public String productId;

  public String productName;

}
