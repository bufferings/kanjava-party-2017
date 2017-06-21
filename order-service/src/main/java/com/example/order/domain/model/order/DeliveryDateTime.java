package com.example.order.domain.model.order;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class DeliveryDateTime {

  private LocalDateTime value;

  public DeliveryDateTime(LocalDateTime value) {
    this.setValue(value);
  }

  private void setValue(LocalDateTime value) {
    this.value = value;
  }

}