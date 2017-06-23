DROP TABLE IF EXISTS OrderGroup;
DROP TABLE IF EXISTS OrderItem;

CREATE TABLE OrderGroup
(
  orderGroupId VARCHAR(36) PRIMARY KEY
  ,orderGuestId INT
  ,orderGuestName VARCHAR(255)
  ,status INT
  ,version INT
);

CREATE TABLE OrderItem
(
  orderItemId VARCHAR(36) PRIMARY KEY
  ,orderGroupId VARCHAR(36)
  ,productId VARCHAR(36)
  ,quantity INT
  ,orderDateTime DATETIME
  ,deliveryPersonId INT
  ,deliveryPersonName VARCHAR(30)
  ,deliveryDateTime DATETIME
  ,status INT
);

