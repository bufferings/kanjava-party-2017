package com.example.guest.dao;

import org.seasar.doma.Domain;

@Domain(valueType = Integer.class, factoryMethod = "of")
public enum OrderItemDelivered {

  NOT_DELIVERED(0), DELIVERED(1);

  private final Integer value;

  private OrderItemDelivered(Integer value) {
    this.value = value;
  }

  public static OrderItemDelivered of(Integer value) {
    for (OrderItemDelivered status : OrderItemDelivered.values()) {
      if (status.value.equals(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException();
  }

  public Integer getValue() {
    return value;
  }

}