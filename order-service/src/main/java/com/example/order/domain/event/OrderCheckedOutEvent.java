package com.example.order.domain.event;

import lombok.Data;

@Data
public class OrderCheckedOutEvent implements DomainEvent {

  public final Integer orderGuestId;

}
