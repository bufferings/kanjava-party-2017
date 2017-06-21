SELECT
  orderItemId
  ,orderGroupId
  ,productId
  ,quantity
  ,orderDateTime
  ,deliveryPersonId
  ,deliveryPersonName
  ,deliveryDateTime
  ,status
FROM
  OrderItem
WHERE
  orderGroupId = /* orderGroupId */1
ORDER BY orderDateTime ASC
