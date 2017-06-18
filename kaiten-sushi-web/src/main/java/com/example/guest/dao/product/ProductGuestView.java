package com.example.guest.dao.product;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;

import lombok.ToString;

@Entity
@ToString
public class ProductGuestView {

  @Id
  public String productId;

  public String productName;

  public Integer stockQuantity;

  public Integer version;

}
