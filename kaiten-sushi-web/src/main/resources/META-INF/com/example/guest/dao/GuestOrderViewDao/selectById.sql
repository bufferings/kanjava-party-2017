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
  orderId = /* orderId */1
