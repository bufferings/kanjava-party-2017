SELECT
  orderGroupId
  ,orderGuestId
  ,orderGuestName
  ,status
  ,version
FROM
  OrderGroup
WHERE
  orderGuestId = /* orderGuestId */1
  AND status <> 3
