-- INSERT INTO client_type (uuid, version, type, discount, name) VALUES ('97d19b8a-498d-11ed-b878-0242ac120002', 0, 'Default', 0, 'Default');
-- INSERT INTO client_type (uuid, version, type, discount, name) VALUES ('cc3024f0-498d-11ed-b878-0242ac120002', 0, 'Bronze', 0.05, 'Bronze');
-- INSERT INTO client_type (uuid, version, type, discount, name) VALUES ('d46a5014-498d-11ed-b878-0242ac120002', 0, 'Silver', 0.1, 'Silver');
-- INSERT INTO client_type (uuid, version, type, discount, name) VALUES ('dc71cfee-498d-11ed-b878-0242ac120002', 0, 'Gold', 0.15, 'Gold');
INSERT INTO client_type (version, type, discount, name) VALUES (0, 'Default', 0, 'Default');
INSERT INTO client_type (version, type, discount, name) VALUES (0, 'Bronze', 0.05, 'Bronze');
INSERT INTO client_type (version, type, discount, name) VALUES (0, 'Silver', 0.1, 'Silver');
INSERT INTO client_type (version, type, discount, name) VALUES (0, 'Gold', 0.15, 'Gold');

INSERT INTO users (version, type, username, active) VALUES (0, 'Admin', 'admin', true);
INSERT INTO users (version, type, username, first_name, last_name, personal_id, city, street, house_number, client_type, active) VALUES (0, 'Client', 'client', 'Mariusz', 'Pudzianowski', '54352353', 'Warszawa', 'Stalowa', 16, 1, true);

INSERT INTO users (version, type, username, first_name, last_name, active) VALUES (0, 'Employee', 'employee', 'Robert', 'Lewandowski', true);

INSERT INTO room (version, price, room_number, size) VALUES (0, 250.00, 643, 6);
INSERT INTO room (version, price, room_number, size) VALUES (0, 707.19, 836, 1);
INSERT INTO room (version, price, room_number, size) VALUES (0, 170.08, 644, 2);
INSERT INTO room (version, price, room_number, size) VALUES (0, 821.95, 504, 9);
INSERT INTO room (version, price, room_number, size) VALUES (0, 92.27, 957, 6);
INSERT INTO room (version, price, room_number, size) VALUES (0, 1471.35, 958, 5);
INSERT INTO room (version, price, room_number, size) VALUES (0, 736.75, 498, 5);
INSERT INTO room (version, price, room_number, size) VALUES (0, 956.51, 792, 4);
INSERT INTO room (version, price, room_number, size) VALUES (0, 1396.79, 392, 9);
INSERT INTO room (version, price, room_number, size) VALUES (0, 541.56, 244, 4);
INSERT INTO room (version, price, room_number, size) VALUES (0, 353.4, 598, 9);
INSERT INTO room (version, price, room_number, size) VALUES (0, 638.8, 380, 4);
INSERT INTO room (version, price, room_number, size) VALUES (0, 903.54, 793, 5);
INSERT INTO room (version, price, room_number, size) VALUES (0, 352.18, 372, 8);
INSERT INTO room (version, price, room_number, size) VALUES (0, 785.55, 124, 8);

INSERT INTO rent (version, begin_time, board, end_time, final_cost, client_id, room_id) VALUES (0, '2023-10-02 11:00:00.000000', true, '2023-10-05 10:00:00.000000', 1000, 2, 1);
INSERT INTO rent (version, begin_time, board, end_time, final_cost, client_id, room_id) VALUES (0, '2023-10-06 11:00:00.000000', true, '2023-10-10 10:00:00.000000', 2000, 2, 1);
INSERT INTO rent (version, begin_time, board, end_time, final_cost, client_id, room_id) VALUES (0, '2023-10-11 11:00:00.000000', true, '2023-10-21 10:00:00.000000', 3000, 2, 1);
