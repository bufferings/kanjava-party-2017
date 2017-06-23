SELECT
  a.orderItemId
  ,a.orderGuestId
  ,a.orderGuestName
  ,a.productId
  ,b.productName
  ,a.quantity
  ,a.orderedOn
FROM
  StaffOrderItem AS a
    LEFT JOIN StaffProduct AS b
      ON a.productId = b.productId
ORDER BY
  a.orderedOn ASC

