INSERT INTO member (name, email, password, address, role)
VALUES ('John Doe', 'admin@example.com', '$2a$12$NiP8ZlFx6bcXIvXwWy0BtOEu2ZWbg4IfHGaUJhB6d.Epygv5Xba5q', '123 Main St', 'ADMIN');
INSERT INTO member (name, email, password, address, role)
VALUES ('User', 'user@example.com', '$2a$12$NiP8ZlFx6bcXIvXwWy0BtOEu2ZWbg4IfHGaUJhB6d.Epygv5Xba5q', '123 Main St', 'USER');
INSERT INTO brand (brand_id, reg_time, update_time, created_by, modified_by, brand_code, brand_nm, brand_status)
VALUES(1,null,null,'admin@example.com','admin@example.com','B0000001','brand1',true);
INSERT INTO brand (brand_id, reg_time, update_time, created_by, modified_by, brand_code, brand_nm, brand_status)
VALUES(2,null,null,'admin@example.com','admin@example.com','B0000002','brand2',true);
INSERT  INTO  item (item_id, reg_time, update_time, created_by, modified_by, category,item_code , item_detail, item_display_status, item_nm, item_sell_status, price, stock_number,subcategory1,subcategory2, brand_id)
VALUES(1,'2024-06-15 16:35:15.352965','2024-06-15 16:35:15.352965','admin@example.com','admin@example.com','패션의류/잡화','I0000001' ,1,'NOT_DISPLAY',1,'SOLD_OUT',1,1,'남성' ,'티셔츠' ,1);
INSERT  INTO  item (item_id, reg_time, update_time, created_by, modified_by, category, item_code,item_detail, item_display_status, item_nm, item_sell_status, price, stock_number,subcategory1,subcategory2, brand_id)
VALUES (2,'2024-06-15 16:36:11.467419','2024-06-15 16:36:11.467419','admin@example.com','admin@example.com','패션의류/잡화','I0000002' ,2,'NOT_DISPLAY',2,'SOLD_OUT',1,1,'남성' ,'스웻셔츠/후드' ,1);