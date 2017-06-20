package com.example.order.domain.model.order;

import org.springframework.util.Assert;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class OrderGuestId {

  private int value;

  public OrderGuestId(int value) {
    this.setValue(value);
  }

  private void setValue(int value) {
    Assert.isTrue(value >= 0);
    this.value = value;
  }

}
