SELECT
  orderId
  ,orderGroupId
  ,orderGuestId
  ,orderGuestName
  ,productId
  ,productName
  ,quantity
  ,orderDateTime
FROM
  OrderStaffView
WHERE
  orderId = /* orderId */1
ORDER BY
  orderDateTime ASC
