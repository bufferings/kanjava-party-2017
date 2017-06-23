package com.example.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@ToString
public class InventoryRecoveryRequestedEvent {
  public final String eventId;
  public final String productId;
}
