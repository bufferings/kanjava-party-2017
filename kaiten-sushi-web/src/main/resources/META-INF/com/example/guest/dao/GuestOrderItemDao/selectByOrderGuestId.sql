SELECT
  a.orderItemId
  ,a.orderGuestId
  ,a.orderGuestName
  ,a.productId
  ,b.productName
  ,a.quantity
  ,a.orderedOn
  ,a.delivered
  ,a.deliveryPersonId
  ,a.deliveryPersonName
  ,a.deliveredOn
FROM
  GuestOrderItem AS a
    LEFT JOIN GuestProduct AS b
      ON a.productId = b.productId
WHERE
  a.orderGuestId = /* orderGuestId */1
ORDER BY
  a.orderedOn DESC

