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
  OrderGuestView
WHERE
  orderId = /* orderId */1
