SELECT
  orderItemId
  ,orderGuestId
  ,orderGuestName
  ,productId
  ,productName
  ,quantity
  ,orderedOn
  ,delivered
  ,deliveryPersonId
  ,deliveryPersonName
  ,deliveredOn
FROM
  GuestOrderItem
WHERE
  orderGuestId = /* orderGuestId */1
ORDER BY
  orderedOn DESC
