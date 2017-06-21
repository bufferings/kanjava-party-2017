DROP TABLE IF EXISTS GuestProduct;
DROP TABLE IF EXISTS GuestOrderItem;

DROP TABLE IF EXISTS StaffOrderItem;

CREATE TABLE GuestProduct
(
  productId VARCHAR(36) PRIMARY KEY
  ,productName VARCHAR(30)
);

CREATE TABLE GuestOrderItem
(
  orderItemId VARCHAR(36) PRIMARY KEY
  ,orderGuestId INT
  ,orderGuestName VARCHAR(30)
  ,productId VARCHAR(36)
  ,productName VARCHAR(30)
  ,quantity INT
  ,orderedOn DATETIME
  ,delivered INT
  ,deliveryPersonId INT
  ,deliveryPersonName VARCHAR(30)
  ,deliveredOn DATETIME
  ,INDEX idx1(orderGuestId, orderedOn)
);

CREATE TABLE StaffOrderItem
(
  orderItemId VARCHAR(36) PRIMARY KEY
  ,orderGuestId INT
  ,orderGuestName VARCHAR(30)
  ,productId VARCHAR(36)
  ,productName VARCHAR(30)
  ,quantity INT
  ,orderedOn DATETIME
  ,INDEX idx1(orderGuestId)
  ,INDEX idx2(orderedOn)
);
