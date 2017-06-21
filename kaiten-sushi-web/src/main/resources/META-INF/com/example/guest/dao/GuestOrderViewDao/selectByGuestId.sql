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
  orderGuestId = /* orderGuestId */1
ORDER BY
  orderDateTime DESC
