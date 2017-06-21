package com.example.order.domain.event;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrderItemCreatedEvent implements DomainEvent {

  public final String orderItemId;

  public final String orderGroupId;

  public final Integer orderGuestId;

  public final String orderGuestName;

  public final String productId;

  public final String productName;

  public final Integer quantity;

  public final LocalDateTime orderedOn;

}