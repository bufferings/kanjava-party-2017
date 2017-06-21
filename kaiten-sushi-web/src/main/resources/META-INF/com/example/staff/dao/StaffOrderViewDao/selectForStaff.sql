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
  StaffOrderView
ORDER BY
  orderDateTime ASC
