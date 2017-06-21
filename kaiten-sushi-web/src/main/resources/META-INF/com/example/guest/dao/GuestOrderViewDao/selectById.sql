SELECT
  orderId
  ,orderGroupId
  ,orderGuestId
  ,orderGuestName
  ,productId
  ,productName
  ,quantity
  ,orderDateTime
  ,delivered
FROM
  GuestOrderView
WHERE
  orderId = /* orderId */1
