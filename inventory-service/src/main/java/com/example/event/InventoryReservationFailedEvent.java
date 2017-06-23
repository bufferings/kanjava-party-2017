package com.example.event;

import com.example.util.IdUtil;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@ToString
public class InventoryReservationFailedEvent {

  public static InventoryReservationFailedEvent from(InventoryReservationRequestedEvent requestedEvent) {
    return InventoryReservationFailedEvent.builder()
        .eventId(IdUtil.generateId())
        .productId(requestedEvent.productId)
        .reservationCount(requestedEvent.reservationCount)
        .requestEventId(requestedEvent.eventId)
        .build();
  }

  public final String eventId;
  public final String productId;
  public final Integer reservationCount;
  public final String requestEventId;
}
