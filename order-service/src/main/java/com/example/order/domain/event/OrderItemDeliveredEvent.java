package com.example.order.domain.event;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrderItemDeliveredEvent implements DomainEvent {

  public final String orderItemId;

  public final Integer deliveryPersonId;

  public final String deliveryPersonName;

  public final LocalDateTime deliveredOn;

}
