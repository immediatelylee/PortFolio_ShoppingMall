INSERT INTO member (name, email, password,phone, zone_code,road_address,detail_address ,role)
VALUES ('John Doe', 'admin@example.com', '$2a$12$NiP8ZlFx6bcXIvXwWy0BtOEu2ZWbg4IfHGaUJhB6d.Epygv5Xba5q','010-1234-5678' ,'11111','경기 성남시 분당구 판교역로 166','101동304호', 'ADMIN');
INSERT INTO member (name, email, password,phone, zone_code,road_address,detail_address ,role)
VALUES ('User', 'user@example.com', '$2a$12$NiP8ZlFx6bcXIvXwWy0BtOEu2ZWbg4IfHGaUJhB6d.Epygv5Xba5q','010-3456-7890' ,'22222', '경기 성남시 분당구 판교역로 166','101동304호', 'USER');

INSERT INTO brand (brand_id, reg_time, update_time, created_by, modified_by, brand_code, brand_nm, brand_status)
VALUES(1,'2024-06-15 16:35:15.352965','2024-06-15 16:35:15.352965','admin@example.com','admin@example.com','B0000001','brand1',true);
INSERT INTO brand (brand_id, reg_time, update_time, created_by, modified_by, brand_code, brand_nm, brand_status)
VALUES(2,'2024-06-15 16:35:15.352965','2024-06-15 16:35:15.352965','admin@example.com','admin@example.com','B0000002','brand2',true);

INSERT  INTO  item (item_id, reg_time, update_time, created_by, modified_by, main_category,item_code , item_detail, item_display_status, item_nm, item_sell_status, price, stock_number,sub_category,sub_sub_category, brand_id)
VALUES(1,'2024-06-15 16:35:15.352965','2024-06-15 16:35:15.352965','admin@example.com','admin@example.com','패션의류/잡화','I0000001' ,1,'DISPLAY','Enfant_CLASSIC_LOGO_KNIT_PULLOVER','SELL',43000,1,'아동' ,'여아' ,1)
,(2,'2024-06-15 16:36:11.467419','2024-06-15 16:36:11.467419','admin@example.com','admin@example.com','패션의류/잡화','I0000002',2,'DISPLAY','Enfant_mari_bear_embroidery_hoodie','SELL',28000,1,'아동','여아' ,1)
,(3,'2024-06-28 13:33:41.084926','2024-06-28 13:33:41.084926','admin@example.com','admin@example.com','패션의류/잡화','I0000003','red','DISPLAY','Enfant_CLASSIC_LOGO_KNIT_PULLOVER_red','SELL',43000,100,'아동','여아' ,1)
,(4,'2024-06-28 13:33:41.084926','2024-06-28 13:33:41.084926','admin@example.com','admin@example.com','패션의류/잡화','I0000004','ENFANT SEERSUCKER BUCKET HAT sky blue','DISPLAY','ENFANT SEERSUCKER BUCKET HAT sky blue','SELL',1000,100,'아동','여아' ,1);




INSERT  INTO item_detail_img(item_detail_img_id, reg_time, update_time, created_by, modified_by, img_name, img_url, ori_img_name, item_id)
VALUES (1,'2024-06-27 11:44:04.214402','2024-06-27 11:44:04.214402','admin@example.com','admin@example.com','3e2ab645-85c8-452c-b1f0-6be59ced1a03.jpg','/images/item/sample_detail/3e2ab645-85c8-452c-b1f0-6be59ced1a03.jpg','d1.jpg',1)
,(2,'2024-06-27 11:44:04.222382','2024-06-27 11:44:04.222382','admin@example.com','admin@example.com','50087d0d-9b73-4a12-a3cf-4a4473abdae1.jpg','/images/item/sample_detail/50087d0d-9b73-4a12-a3cf-4a4473abdae1.jpg','d2.jpg',1)
,(3,'2024-06-27 11:44:04.228368','2024-06-27 11:44:04.228368','admin@example.com','admin@example.com','883a8630-6dfd-41d6-9c60-d02b3fbd8ac2.jpg','/images/item/sample_detail/883a8630-6dfd-41d6-9c60-d02b3fbd8ac2.jpg','d3.jpg',1)
,(4,'2024-06-27 11:45:29.951826','2024-06-27 11:45:29.951826','admin@example.com','admin@example.com','3c7b7c76-392c-4f68-a7a8-efcda16e99f7.jpg','/images/item/sample_detail/3c7b7c76-392c-4f68-a7a8-efcda16e99f7.jpg','d1.jpg',2)
,(5,'2024-06-27 11:45:29.960806','2024-06-27 11:45:29.960806','admin@example.com','admin@example.com','84b194f8-25dc-4821-8fb8-8b0f2be87ee4.jpg','/images/item/sample_detail/84b194f8-25dc-4821-8fb8-8b0f2be87ee4.jpg','d2.jpg',2)
,(6,'2024-06-27 11:45:29.970776','2024-06-27 11:45:29.970776','admin@example.com','admin@example.com','7079cf8d-2d31-4366-bc55-b99fed5707a4.jpg','/images/item/sample_detail/7079cf8d-2d31-4366-bc55-b99fed5707a4.jpg','d3.jpg',2)
,(7,'2024-06-27 11:45:29.977757','2024-06-27 11:45:29.977757','admin@example.com','admin@example.com','eda86019-8bc4-4d74-b9d1-135c2687690c.jpg','/images/item/sample_detail/eda86019-8bc4-4d74-b9d1-135c2687690c.jpg','d4.jpg',2)
,(8,'2024-06-28 13:33:41.551666','2024-06-28 13:33:41.551666','admin@example.com','admin@example.com','b7028431-1902-4511-ac87-3d44d0e44877.jpg','/images/item/sample_detail/b7028431-1902-4511-ac87-3d44d0e44877.jpg','d1.jpg',3)
,(9,'2024-06-28 13:33:41.565630','2024-06-28 13:33:41.565630','admin@example.com','admin@example.com','311c071d-80a1-4127-acaf-f4c4de3ad7a5.jpg','/images/item/sample_detail/311c071d-80a1-4127-acaf-f4c4de3ad7a5.jpg','d2.jpg',3)
,(10,'2024-06-28 13:33:41.584577','2024-06-28 13:33:41.584577','admin@example.com','admin@example.com','99c2b187-091a-4de8-8ec5-413df60931d5.jpg','/images/item/sample_detail/99c2b187-091a-4de8-8ec5-413df60931d5.jpg','d3.jpg',3)
,(11,'2024-06-28 13:33:41.591559','2024-06-28 13:33:41.591559','admin@example.com','admin@example.com','beba5ea5-9aef-4b6f-9d04-c7fd7e051bd1.jpg','/images/item/sample_detail/beba5ea5-9aef-4b6f-9d04-c7fd7e051bd1.jpg','d4.jpg',3)
,(12,'2024-08-15 23:39:56.828035','2024-08-15 23:39:56.828035','admin@example.com','admin@example.com','734bba80-e261-4eb9-9fb7-1f4f6ae6e15e.jpg','/images/item/sample_detail/734bba80-e261-4eb9-9fb7-1f4f6ae6e15e.jpg','d1.jpg',4)
,(13,'2024-08-15 23:39:56.846016','2024-08-15 23:39:56.846016','admin@example.com','admin@example.com','bb82b737-e3f5-4ee8-bbbe-0ed4941988b1.jpg','/images/item/sample_detail/bb82b737-e3f5-4ee8-bbbe-0ed4941988b1.jpg','d2.jpg',4)
,(14,'2024-08-15 23:39:56.861950','2024-08-15 23:39:56.861950','admin@example.com','admin@example.com','722d2c7d-6c22-4788-a4dc-c14789f28b60.jpg','/images/item/sample_detail/722d2c7d-6c22-4788-a4dc-c14789f28b60.jpg','d3.jpg',4)
,(15,'2024-08-15 23:39:56.868927','2024-08-15 23:39:56.868927','admin@example.com','admin@example.com','9401a81b-065d-4ae2-9cd2-2dbe0e05eaf2.jpg','/images/item/sample_detail/9401a81b-065d-4ae2-9cd2-2dbe0e05eaf2.jpg','d4.jpg',4);

INSERT INTO item_img(item_img_id, reg_time, update_time, created_by, modified_by, img_name, img_url, ori_img_name, repimg_yn, item_id)
VALUES(1,'2024-06-27 11:44:04.196450','2024-06-27 11:44:04.196450','admin@example.com','admin@example.com','d25f7e52-d299-462c-89a8-42dbe2c056cf.jpg','/images/item/sample_img/d25f7e52-d299-462c-89a8-42dbe2c056cf.jpg','enfant_CLASSIC_LOGO_KNIT_PULLOVER_front.jpg','Y',1)
,(2,'2024-06-27 11:45:29.943848','2024-06-27 11:45:29.943848','admin@example.com','admin@example.com','cafd1042-c8ec-4a95-9def-6057c6d03fea.jpg','/images/item/sample_img/cafd1042-c8ec-4a95-9def-6057c6d03fea.jpg','enfant_mari_bear_embroidery_hoodie_front.jpg','Y',2)
,(3,'2024-06-28 13:33:41.540698','2024-06-28 13:33:41.540698','admin@example.com','admin@example.com','61596d04-d0cc-4d83-8f92-7f98a39c043b.jpg','/images/item/sample_img/61596d04-d0cc-4d83-8f92-7f98a39c043b.jpg','enfant_CLASSIC_LOGO_KNIT_PULLOVER_front_red.jpg','Y',3)
,(4,'2024-08-15 23:39:56.813076','2024-08-15 23:39:56.813076','admin@example.com','admin@example.com','551031b8-0419-4e01-9e98-ec03fe326c94.jpg','/images/item/sample_img/551031b8-0419-4e01-9e98-ec03fe326c94.jpg','ENFANT SEERSUCKER BUCKET HAT sky blue.jpg','Y',4);


INSERT INTO item_thumbnail(item_thumbnail_id, img_name, img_url, ori_img_name, repimg_yn, item_id)
VALUES(1,'thumb_b5791dfb-847d-425e-b354-453462e663daenfant_CLASSIC_LOGO_KNIT_PULLOVER_front.jpg','/images/item/sample_thumb/thumb_b5791dfb-847d-425e-b354-453462e663daenfant_CLASSIC_LOGO_KNIT_PULLOVER_front.jpg','enfant_CLASSIC_LOGO_KNIT_PULLOVER_front.jpg',null,1)
,(2,'thumb_1e5d2dd2-a495-42b0-acf5-35a68fdde29aenfant_mari_bear_embroidery_hoodie_front.jpg','/images/item/sample_thumb/thumb_1e5d2dd2-a495-42b0-acf5-35a68fdde29aenfant_mari_bear_embroidery_hoodie_front.jpg','enfant_mari_bear_embroidery_hoodie_front.jpg',null,2)
,(3,'thumb_6e90e00e-75b3-403a-aece-6496a493d944enfant_CLASSIC_LOGO_KNIT_PULLOVER_front_red.jpg','/images/item/sample_thumb/thumb_6e90e00e-75b3-403a-aece-6496a493d944enfant_CLASSIC_LOGO_KNIT_PULLOVER_front_red.jpg','enfant_CLASSIC_LOGO_KNIT_PULLOVER_front_red.jpg',null,3)
,(4,'thumb_199a6f08-5f94-4059-854f-9baf5a9b9f89ENFANT SEERSUCKER BUCKET HAT sky blue.jpg','/images/item/sample_thumb/thumb_199a6f08-5f94-4059-854f-9baf5a9b9f89ENFANT SEERSUCKER BUCKET HAT sky blue.jpg','ENFANT SEERSUCKER BUCKET HAT sky blue.jpg',null,4);


-- 1) OptionSet
INSERT INTO option_set(id, name) VALUES(1, '색상/사이즈');
INSERT INTO option_set(id, name) VALUES(2, '재질/두께');

-- 2) Option
-- 첫 번째 세트(1)에 대해 "색상", "사이즈"
INSERT INTO shop_option(id, name, code, option_set_id, option_index) VALUES(101, '색상', 'O0000001', 1, 0);
INSERT INTO shop_option(id, name, code, option_set_id, option_index) VALUES(102, '사이즈', 'O0000002', 1, 1);

-- 두 번째 세트(2)에 대해 "재질", "두께"
INSERT INTO shop_option(id, name, code, option_set_id, option_index) VALUES(201, '재질', 'O0000003', 2, 0);
INSERT INTO shop_option(id, name, code, option_set_id, option_index) VALUES(202, '두께', 'O0000004', 2, 1);

-- 3) OptionValue
-- (A) 색상: 블랙, 화이트
INSERT INTO option_value(id, value, option_id, order_index) VALUES(1001, '블랙', 101, 0);
INSERT INTO option_value(id, value, option_id, order_index) VALUES(1002, '화이트', 101, 1);

-- (B) 사이즈: S, M, L
INSERT INTO option_value(id, value, option_id, order_index) VALUES(1003, 'S', 102, 0);
INSERT INTO option_value(id, value, option_id, order_index) VALUES(1004, 'M', 102, 1);
INSERT INTO option_value(id, value, option_id, order_index) VALUES(1005, 'L', 102, 2);

-- (C) 재질: 면, 폴리에스터
INSERT INTO option_value(id, value, option_id, order_index) VALUES(2001, '면', 201, 0);
INSERT INTO option_value(id, value, option_id, order_index) VALUES(2002, '폴리에스터', 201, 1);

-- (D) 두께: 얇음, 보통, 두꺼움
INSERT INTO option_value(id, value, option_id, order_index) VALUES(2003, '얇음', 202, 0);
INSERT INTO option_value(id, value, option_id, order_index) VALUES(2004, '보통', 202, 1);
INSERT INTO option_value(id, value, option_id, order_index) VALUES(2005, '두꺼움', 202, 2);