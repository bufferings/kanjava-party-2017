package com.example.order.domain.event;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrderDeliveredEvent implements DomainEvent {

  public final String orderGroupId;

  public final String orderId;

  public final Integer deliveryPersonId;

  public final String deliveryPersonName;

  public final LocalDateTime deliveryDateTime;

}
