SELECT
  orderId
  ,orderGroupId
  ,orderGuestId
  ,orderGuestName
  ,productId
  ,productName
  ,quantity
  ,orderDateTime
  ,deliveryPersonId
  ,deliveryPersonName
  ,deliveryDateTime
  ,delivered
FROM
  GuestOrderView
WHERE
  orderGuestId = /* orderGuestId */1
ORDER BY
  orderDateTime DESC
