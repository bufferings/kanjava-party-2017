package com.example.order.domain.model.order;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class DeliveryPersonId {

  private int value;

  public DeliveryPersonId(int value) {
    this.setValue(value);
  }

  private void setValue(int value) {
    this.value = value;
  }

}
