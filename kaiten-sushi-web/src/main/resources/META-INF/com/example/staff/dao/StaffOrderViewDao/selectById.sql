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
WHERE
  orderId = /* orderId */1
ORDER BY
  orderDateTime ASC
