INSERT INTO member (name, email, password, address, role)
VALUES ('John Doe', 'admin@example.com', '$2a$12$NiP8ZlFx6bcXIvXwWy0BtOEu2ZWbg4IfHGaUJhB6d.Epygv5Xba5q', '123 Main St', 'ADMIN');
INSERT INTO member (name, email, password, address, role)
VALUES ('User', 'user@example.com', '$2a$12$NiP8ZlFx6bcXIvXwWy0BtOEu2ZWbg4IfHGaUJhB6d.Epygv5Xba5q', '123 Main St', 'USER');
INSERT INTO brand (brand_id, reg_time, update_time, created_by, modified_by, brand_code, brand_nm, brand_status)
VALUES(1,null,null,'admin@example.com','admin@example.com','B0000001','brand1',true);
INSERT INTO brand (brand_id, reg_time, update_time, created_by, modified_by, brand_code, brand_nm, brand_status)
VALUES(2,null,null,'admin@example.com','admin@example.com','B0000002','brand2',true);