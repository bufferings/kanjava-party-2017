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
  orderGuestId = /* orderGuestId */1
ORDER BY
  orderDateTime DESC
