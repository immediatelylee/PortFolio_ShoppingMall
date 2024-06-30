INSERT INTO member (name, email, password, address, role)
VALUES ('John Doe', 'admin@example.com', '$2a$12$NiP8ZlFx6bcXIvXwWy0BtOEu2ZWbg4IfHGaUJhB6d.Epygv5Xba5q', '123 Main St', 'ADMIN');
INSERT INTO member (name, email, password, address, role)
VALUES ('User', 'user@example.com', '$2a$12$NiP8ZlFx6bcXIvXwWy0BtOEu2ZWbg4IfHGaUJhB6d.Epygv5Xba5q', '123 Main St', 'USER');

INSERT INTO brand (brand_id, reg_time, update_time, created_by, modified_by, brand_code, brand_nm, brand_status)
VALUES(1,null,null,'admin@example.com','admin@example.com','B0000001','brand1',true);
INSERT INTO brand (brand_id, reg_time, update_time, created_by, modified_by, brand_code, brand_nm, brand_status)
VALUES(2,null,null,'admin@example.com','admin@example.com','B0000002','brand2',true);
INSERT  INTO  item (item_id, reg_time, update_time, created_by, modified_by, main_category,item_code , item_detail, item_display_status, item_nm, item_sell_status, price, stock_number,sub_category,sub_sub_category, brand_id)
VALUES(1,'2024-06-15 16:35:15.352965','2024-06-15 16:35:15.352965','admin@example.com','admin@example.com','패션의류/잡화','I0000001' ,1,'NOT_DISPLAY',1,'SOLD_OUT',1,1,'남성' ,'티셔츠' ,1)
,(2,'2024-06-15 16:36:11.467419','2024-06-15 16:36:11.467419','admin@example.com','admin@example.com','패션의류/잡화','I0000002',2,'NOT_DISPLAY',2,'SOLD_OUT',1,1,'남성','스웻셔츠/후드' ,1)
,(3,'2024-06-28 13:33:41.084926','2024-06-28 13:33:41.084926','admin@example.com','admin@example.com','패션의류/잡화','I0000003','red','NOT_DISPLAY',2,'SOLD_OUT',43000,100,'남성','티셔츠' ,1);




INSERT  INTO item_detail_img(item_detail_img_id, reg_time, update_time, created_by, modified_by, img_name, img_url, ori_img_name, item_id)
VALUES (1,'2024-06-27 11:44:04.214402','2024-06-27 11:44:04.214402','admin@example.com','admin@example.com','3e2ab645-85c8-452c-b1f0-6be59ced1a03.jpg','/images/item/sample_datail/3e2ab645-85c8-452c-b1f0-6be59ced1a03.jpg','d1.jpg',1)
,(2,'2024-06-27 11:44:04.222382','2024-06-27 11:44:04.222382','admin@example.com','admin@example.com','50087d0d-9b73-4a12-a3cf-4a4473abdae1.jpg','/images/item/sample_datail/50087d0d-9b73-4a12-a3cf-4a4473abdae1.jpg','d2.jpg',1)
,(3,'2024-06-27 11:44:04.228368','2024-06-27 11:44:04.228368','admin@example.com','admin@example.com','883a8630-6dfd-41d6-9c60-d02b3fbd8ac2.jpg','/images/item/sample_datail/883a8630-6dfd-41d6-9c60-d02b3fbd8ac2.jpg','d3.jpg',1)
,(4,'2024-06-27 11:45:29.951826','2024-06-27 11:45:29.951826','admin@example.com','admin@example.com','3c7b7c76-392c-4f68-a7a8-efcda16e99f7.jpg','/images/item/sample_datail/3c7b7c76-392c-4f68-a7a8-efcda16e99f7.jpg','d1.jpg',2)
,(5,'2024-06-27 11:45:29.960806','2024-06-27 11:45:29.960806','admin@example.com','admin@example.com','84b194f8-25dc-4821-8fb8-8b0f2be87ee4.jpg','/images/item/sample_datail/84b194f8-25dc-4821-8fb8-8b0f2be87ee4.jpg','d2.jpg',2)
,(6,'2024-06-27 11:45:29.970776','2024-06-27 11:45:29.970776','admin@example.com','admin@example.com','7079cf8d-2d31-4366-bc55-b99fed5707a4.jpg','/images/item/sample_datail/7079cf8d-2d31-4366-bc55-b99fed5707a4.jpg','d3.jpg',2)
,(7,'2024-06-27 11:45:29.977757','2024-06-27 11:45:29.977757','admin@example.com','admin@example.com','eda86019-8bc4-4d74-b9d1-135c2687690c.jpg','/images/item/sample_datail/eda86019-8bc4-4d74-b9d1-135c2687690c.jpg','d4.jpg',2)
,(8,'2024-06-28 13:33:41.551666','2024-06-28 13:33:41.551666','admin@example.com','admin@example.com','b7028431-1902-4511-ac87-3d44d0e44877.jpg','/images/item/sample_datail/b7028431-1902-4511-ac87-3d44d0e44877.jpg','d1.jpg',3)
,(9,'2024-06-28 13:33:41.565630','2024-06-28 13:33:41.565630','admin@example.com','admin@example.com','311c071d-80a1-4127-acaf-f4c4de3ad7a5.jpg','/images/item/sample_datail/311c071d-80a1-4127-acaf-f4c4de3ad7a5.jpg','d2.jpg',3)
,(10,'2024-06-28 13:33:41.584577','2024-06-28 13:33:41.584577','admin@example.com','admin@example.com','99c2b187-091a-4de8-8ec5-413df60931d5.jpg','/images/item/sample_datail/99c2b187-091a-4de8-8ec5-413df60931d5.jpg','d3.jpg',3)
,(11,'2024-06-28 13:33:41.591559','2024-06-28 13:33:41.591559','admin@example.com','admin@example.com','beba5ea5-9aef-4b6f-9d04-c7fd7e051bd1.jpg','/images/item/sample_datail/beba5ea5-9aef-4b6f-9d04-c7fd7e051bd1.jpg','d4.jpg',3);

INSERT INTO item_img(item_img_id, reg_time, update_time, created_by, modified_by, img_name, img_url, ori_img_name, repimg_yn, item_id)
VALUES(1,'2024-06-27 11:44:04.196450','2024-06-27 11:44:04.196450','admin@example.com','admin@example.com','d25f7e52-d299-462c-89a8-42dbe2c056cf.jpg','/images/item/sample_img/d25f7e52-d299-462c-89a8-42dbe2c056cf.jpg','enfant_CLASSIC_LOGO_KNIT_PULLOVER_front.jpg','Y',1)
,(2,'2024-06-27 11:45:29.943848','2024-06-27 11:45:29.943848','admin@example.com','admin@example.com','cafd1042-c8ec-4a95-9def-6057c6d03fea.jpg','/images/item/sample_img/cafd1042-c8ec-4a95-9def-6057c6d03fea.jpg','enfant_mari_bear_embroidery_hoodie_front.jpg','Y',2)
,(3,'2024-06-28 13:33:41.540698','2024-06-28 13:33:41.540698','admin@example.com','admin@example.com','61596d04-d0cc-4d83-8f92-7f98a39c043b.jpg','/images/item/sample_img/61596d04-d0cc-4d83-8f92-7f98a39c043b.jpg','enfant_CLASSIC_LOGO_KNIT_PULLOVER_front_red.jpg','Y',3);


INSERT INTO item_thumbnail(item_thumbnail_id, img_name, img_url, ori_img_name, repimg_yn, item_id)
VALUES(1,'thumb_b5791dfb-847d-425e-b354-453462e663daenfant_CLASSIC_LOGO_KNIT_PULLOVER_front.jpg','/images/item/sample_thumb/thumb_b5791dfb-847d-425e-b354-453462e663daenfant_CLASSIC_LOGO_KNIT_PULLOVER_front.jpg','enfant_CLASSIC_LOGO_KNIT_PULLOVER_front.jpg',null,1)
,(2,'thumb_1e5d2dd2-a495-42b0-acf5-35a68fdde29aenfant_mari_bear_embroidery_hoodie_front.jpg','/images/item/sample_thumb/thumb_1e5d2dd2-a495-42b0-acf5-35a68fdde29aenfant_mari_bear_embroidery_hoodie_front.jpg','enfant_mari_bear_embroidery_hoodie_front.jpg',null,2)
,(3,'thumb_6e90e00e-75b3-403a-aece-6496a493d944enfant_CLASSIC_LOGO_KNIT_PULLOVER_front_red.jpg','/images/item/sample_thumb/thumb_6e90e00e-75b3-403a-aece-6496a493d944enfant_CLASSIC_LOGO_KNIT_PULLOVER_front_red.jpg','enfant_CLASSIC_LOGO_KNIT_PULLOVER_front_red.jpg',null,3);

