truncate table msa_order cascade;
truncate table msa_user cascade;
truncate table order_history cascade;
truncate table order_item cascade;
truncate table product cascade;

INSERT INTO msa_user (id,status,pickup_address,pickup_start_time,pickup_end_time,created_at,modified_at) VALUES
	 ('1316b012-a8c6-4adc-b1cd-0b4c93e0ab5a'::uuid,'CREATED',NULL,NULL,NULL,NULL,'2025-11-24 18:29:34.0538'),
	 ('3229ddc5-6c8f-4f22-8782-772d112815b9'::uuid,'CREATED',NULL,NULL,NULL,NULL,'2025-11-24 18:29:35.249402'),
	 ('45838ca0-584e-443d-afb6-ed6b158892a3'::uuid,'CREATED',NULL,NULL,NULL,NULL,'2025-11-24 18:29:35.546373'),
	 ('8aba8007-ccaf-436c-a198-7ecd10f45da0'::uuid,'CREATED',NULL,NULL,NULL,NULL,'2025-11-24 18:29:35.647995'),
	 ('fc9b4e3a-5a08-40b8-b03f-4e47f3bb5c1f'::uuid,'CREATED',NULL,NULL,NULL,NULL,'2025-11-24 18:29:35.657184'),
	 ('977242e2-d0b5-4b8b-be4d-bf7809600f7c'::uuid,'CREATED',NULL,NULL,NULL,NULL,'2025-11-24 18:29:35.848549'),
	 ('f2c526f9-acd1-4535-a27b-239caed50313'::uuid,'CREATED',NULL,NULL,NULL,NULL,'2025-11-24 18:29:35.857369');

INSERT INTO product (id,"name",product_id,user_id,price,deposit_sum,quantity,currency,created_at,modified_at) VALUES
	 ('a44f355b-8bdf-4cd8-bda1-b2cc34812cc3'::uuid,'Палатка 3х местная','c1716965-baef-4adc-8002-92200d07b58f'::uuid,'8aba8007-ccaf-436c-a198-7ecd10f45da0'::uuid,2000.0,1000.0,12,'RUB','2025-11-23 13:16:38.458815','2025-11-23 13:16:38.458815'),
	 ('14551132-16c8-457a-ac0c-a258a8e4f935'::uuid,'Палатка 2х местная','2a6629c1-2180-4a7e-a0ce-305f46cc4b71'::uuid,'8aba8007-ccaf-436c-a198-7ecd10f45da0'::uuid,1000.0,500.0,11,'RUB',NULL,'2025-11-24 19:02:36.544517');

INSERT INTO msa_order (id,user_id,manager_id,currency,status,"name",rent_start_date,rent_complete_date,reject_reason,created_at,modified_at) VALUES
	 ('4e232b78-8183-484a-9d50-2b693f8d99b2'::uuid,'f2c526f9-acd1-4535-a27b-239caed50313'::uuid,'8aba8007-ccaf-436c-a198-7ecd10f45da0'::uuid,'RUB','WAITING_RESERVE_PRODUCT','Заказ №1','21.11.2025','21.12.2025',NULL,'2025-11-24 19:02:34.381591','2025-11-24 19:02:35.833931');

INSERT INTO order_history (id,order_id,status,created_at,modified_at) VALUES
	 ('97669548-4cd8-4b83-9597-f94e5ac9089a'::uuid,'4e232b78-8183-484a-9d50-2b693f8d99b2'::uuid,'CREATED','2025-11-24 19:02:34.382739','2025-11-24 19:02:34.382739');

INSERT INTO order_item (id,order_id,product_id,quantity,price,deposit_sum,created_at,modified_at) VALUES
	 ('c10f986e-1e06-48da-8f12-da0818801c17'::uuid,'4e232b78-8183-484a-9d50-2b693f8d99b2'::uuid,'2a6629c1-2180-4a7e-a0ce-305f46cc4b71'::uuid,1,1000.0,500.0,'2025-11-24 19:02:34.382205','2025-11-24 19:02:34.382205');

