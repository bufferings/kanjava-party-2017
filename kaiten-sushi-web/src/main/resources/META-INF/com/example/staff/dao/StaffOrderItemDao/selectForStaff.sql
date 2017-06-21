SELECT
  orderItemId
  ,orderGuestId
  ,orderGuestName
  ,productId
  ,productName
  ,quantity
  ,orderedOn
FROM
  StaffOrderItem
ORDER BY
  orderedOn ASC
