package com.example.order.domain.event;

import lombok.Data;

@Data
public class OrderGroupCheckedOutEvent implements DomainEvent {

  public final String orderGroupId;

}
