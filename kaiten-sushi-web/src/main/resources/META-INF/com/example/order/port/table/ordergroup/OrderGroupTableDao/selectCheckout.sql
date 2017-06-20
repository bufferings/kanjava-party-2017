SELECT
  orderGroupId
  ,orderGuestId
  ,orderGuestName
  ,status
  ,version
FROM
  OrderGroup
WHERE
  status = 2
