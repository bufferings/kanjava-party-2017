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
  orderItemId = /* orderItemId */1
