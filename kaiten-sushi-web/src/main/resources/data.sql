DELETE FROM GuestProduct;
DELETE FROM GuestOrderItem;

DELETE FROM StaffProduct;
DELETE FROM StaffOrderItem;

INSERT INTO GuestProduct VALUES
   ('3926d0ad-5638-4ed1-b16a-b09197ddcc10', 'まぐろ')
  ,('1d8bcd93-2b9c-4225-a525-8f750d4c444c', 'サーモン')
  ,('7e570f08-65bd-4b1f-be75-3d977d818023', 'いくら')
;

INSERT INTO StaffProduct VALUES
   ('3926d0ad-5638-4ed1-b16a-b09197ddcc10', 'まぐろ')
  ,('1d8bcd93-2b9c-4225-a525-8f750d4c444c', 'サーモン')
  ,('7e570f08-65bd-4b1f-be75-3d977d818023', 'いくら')
;

