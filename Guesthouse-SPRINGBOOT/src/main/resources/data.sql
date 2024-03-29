INSERT INTO client_type (version, type, discount, name) VALUES (0, 'Default', 0, 'Default');
INSERT INTO client_type (version, type, discount, name) VALUES (0, 'Bronze', 0.05, 'Bronze');
INSERT INTO client_type (version, type, discount, name) VALUES (0, 'Silver', 0.1, 'Silver');
INSERT INTO client_type (version, type, discount, name) VALUES (0, 'Gold', 0.15, 'Gold');

INSERT INTO users (version, type, username, active, role) VALUES (0, 'Admin', 'admin17', true, 'ADMIN');
INSERT INTO users (version, type, username, first_name, last_name, personal_id, city, street, house_number, client_type, active, role) VALUES (0, 'Client', 'client', 'Mariusz', 'Pudzianowski', '54352353', 'Warszawa', 'Stalowa', 16, 1, true, 'CLIENT');
INSERT INTO users (version, type, username, first_name, last_name, active, role) VALUES (0, 'Employee', 'employee', 'Robert', 'Lewandowski', true, 'EMPLOYEE');
INSERT INTO users (version, type, username, first_name, last_name, personal_id, city, street, house_number, client_type, active, role) VALUES (0, 'Client', 'jakub2', 'Jakub', 'Bukaj', '3584873', 'Krakow', 'Smutna', 13, 1, false, 'CLIENT');
INSERT INTO users (version, type, username, first_name, last_name, personal_id, city, street, house_number, client_type, active, role) VALUES (0, 'Client', 'jakub3', 'Kuba', 'Bokaj', '3584173', 'Krakow', 'Smutna', 13, 1, false, 'CLIENT');


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
INSERT INTO rent (version, begin_time, board, end_time, final_cost, client_id, room_id) VALUES (0, '2022-10-11 11:00:00.000000', FALSE, '2023-02-01 10:00:00.000000', 30000, 2, 11);
INSERT INTO rent (version, begin_time, board, end_time, final_cost, client_id, room_id) VALUES (0, '2022-05-01 11:00:00.000000', FALSE, '2022-05-09 10:00:00.000000', 3000, 2, 11);
INSERT INTO rent (version, begin_time, board, end_time, final_cost, client_id, room_id) VALUES (0, '2024-10-11 11:00:00.000000', FALSE, '2024-11-21 10:00:00.000000', 3000, 2, 11);
-- INSERT INTO rent (version, begin_time, board, end_time, final_cost, client_id, room_id) VALUES (0, '2020-10-11 11:00:00.000000', FALSE, '2020-11-21 10:00:00.000000', 3000, 2, 11);
