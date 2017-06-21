DROP TABLE IF EXISTS Product;
DROP TABLE IF EXISTS OrderGroup;
DROP TABLE IF EXISTS OrderItem;

DROP TABLE IF EXISTS GuestProductView;
DROP TABLE IF EXISTS GuestOrderView;

DROP TABLE IF EXISTS StaffOrderView;

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

CREATE TABLE GuestProductView
(
  productId VARCHAR(36) PRIMARY KEY
  ,productName VARCHAR(30)
);

CREATE TABLE GuestOrderView
(
  orderId VARCHAR(36) PRIMARY KEY
  ,orderGroupId VARCHAR(36)
  ,orderGuestId INT
  ,orderGuestName VARCHAR(30)
  ,productId VARCHAR(36)
  ,productName VARCHAR(30)
  ,quantity INT
  ,orderDateTime DATETIME
  ,deliveryPersonId INT
  ,deliveryPersonName VARCHAR(30)
  ,deliveryDateTime DATETIME
  ,delivered INT
);

CREATE TABLE StaffOrderView
(
  orderId VARCHAR(36) PRIMARY KEY
  ,orderGroupId VARCHAR(36)
  ,orderGuestId INT
  ,orderGuestName VARCHAR(30)
  ,productId VARCHAR(36)
  ,productName VARCHAR(30)
  ,quantity INT
  ,orderDateTime DATETIME
);
