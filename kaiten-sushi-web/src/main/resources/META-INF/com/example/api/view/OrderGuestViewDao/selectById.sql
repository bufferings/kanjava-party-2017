SELECT
  orderId
  ,orderGroupId
  ,tableNumber
  ,productId
  ,productName
  ,quantity
  ,orderDateTime
  ,delivered
FROM
  OrderGuestView
WHERE
  orderId = /* orderId */1
