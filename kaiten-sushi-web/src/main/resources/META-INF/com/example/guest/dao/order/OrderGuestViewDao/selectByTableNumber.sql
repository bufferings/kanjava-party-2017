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
  tableNumber = /* tableNumber */1
ORDER BY
  orderDateTime DESC
