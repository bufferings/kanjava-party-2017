package com.example.event;

import com.example.util.IdUtil;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@ToString
public class InventoryRecoveredEvent {

  public static InventoryRecoveredEvent from(InventoryRecoveryRequestedEvent requestedEvent) {
    return InventoryRecoveredEvent.builder()
        .eventId(IdUtil.generateId())
        .productId(requestedEvent.productId)
        .requestEventId(requestedEvent.eventId)
        .build();
  }

  public final String eventId;
  public final String productId;
  public final String requestEventId;
}
