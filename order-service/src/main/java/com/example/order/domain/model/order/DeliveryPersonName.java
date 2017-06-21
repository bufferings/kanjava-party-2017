package com.example.order.domain.model.order;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class DeliveryPersonName {

  private String value;

  public DeliveryPersonName(String value) {
    this.setValue(value);
  }

  private void setValue(String value) {
    this.value = value;
  }

}