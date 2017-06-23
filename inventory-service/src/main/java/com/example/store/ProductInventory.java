package com.example.store;

import org.springframework.util.Assert;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@ToString
public class ProductInventory {

  private static final Integer RECOVERY_COUNT = Integer.valueOf(10);

  public String productId;
  public Integer inventoryCount;

  public boolean canAccept(Integer reservationCount) {
    return inventoryCount.intValue() >= reservationCount.intValue();
  }

  public void accept(Integer reservationCount) {
    Assert.isTrue(canAccept(reservationCount), "can't accept");
    inventoryCount = Integer.valueOf(inventoryCount.intValue() - reservationCount.intValue());
  }

  public void recover() {
    inventoryCount = RECOVERY_COUNT;
  }
}
