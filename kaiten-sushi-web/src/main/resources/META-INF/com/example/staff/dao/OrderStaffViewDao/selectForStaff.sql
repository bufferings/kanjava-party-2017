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
ORDER BY
  orderDateTime ASC
