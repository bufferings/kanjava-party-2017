DROP TABLE IF EXISTS Product;
DROP TABLE IF EXISTS OrderGroup;
DROP TABLE IF EXISTS OrderItem;

DROP TABLE IF EXISTS GuestProduct;
DROP TABLE IF EXISTS GuestOrderItem;

DROP TABLE IF EXISTS StaffOrderItem;

CREATE TABLE Product
(
  productId VARCHAR(36) PRIMARY KEY
  ,productName VARCHAR(30)
  ,stockQuantity INT
  ,version INT
);

CREATE TABLE OrderGroup
(
  orderGroupId VARCHAR(36) PRIMARY KEY
  ,orderGuestId INT
  ,orderGuestName VARCHAR(30)
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
