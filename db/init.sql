-- ENUM tanımlamaları

CREATE TYPE user_role_enum AS ENUM ('ADMIN', 'COMPANY_USER', 'OUTSOURCE_USER', 'DRIVER');
CREATE TYPE vehicle_type_enum AS ENUM ('SEDAN', 'SUV', 'TRUCK', 'VAN');
CREATE TYPE vehicle_status_enum AS ENUM ('AVAILABLE', 'ASSIGNED', 'IN_SERVICE', 'OUT_OF_ORDER', 'WAITING_FOR_ASSIGNMENT');
CREATE TYPE ownership_type_enum AS ENUM ('OWNED', 'LEASED');
CREATE TYPE service_type_enum AS ENUM ('REGULAR_MAINTENANCE', 'PART_REPLACEMENT', 'TIRE_CHANGE', 'REPAIR');
CREATE TYPE expense_type_enum AS ENUM ('FUEL', 'MAINTENANCE', 'INSURANCE', 'REPAIR', 'TOTAL');


-- Şirket bilgileri
CREATE TABLE companies (
                           company_id SERIAL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           address TEXT,
                           phone_number VARCHAR(20),
                           tax_number VARCHAR(20) UNIQUE
);

-- Kullanıcı bilgileri
CREATE TABLE users (
                       user_id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,  -- aynı maille birden fazla kullanıcı açılmamalı
                       role user_role_enum NOT NULL,
                       company_id INT REFERENCES companies(company_id)
);

-- Araç bilgileri
CREATE TABLE vehicles (
                          vehicle_id SERIAL PRIMARY KEY,
                          company_id INT REFERENCES companies(company_id),
                          plate_number VARCHAR(20) UNIQUE NOT NULL,
                          brand VARCHAR(50),
                          model VARCHAR(50),
                          year INT,
                          type vehicle_type_enum NOT NULL,
                          status vehicle_status_enum NOT NULL DEFAULT 'AVAILABLE',
                          ownership_type ownership_type_enum NOT NULL,
                          current_odometer NUMERIC(10,2),
                          previous_month_odometer NUMERIC(10,2),
                          is_active BOOLEAN DEFAULT TRUE  -- sistemde aktif olarak kullanılmakta mı?
);

-- Sürücü bilgileri
CREATE TABLE drivers (
                         driver_id SERIAL PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         license_number VARCHAR(50) UNIQUE NOT NULL,
                         license_expiry_date DATE,
                         is_active BOOLEAN DEFAULT TRUE
);

-- Araç - sürücü eşlemesi ve kullanım kaydı
CREATE TABLE vehicle_usage (
                               usage_id SERIAL PRIMARY KEY,
                               vehicle_id INT REFERENCES vehicles(vehicle_id),
                               driver_id INT REFERENCES drivers(driver_id),
                               start_date DATE NOT NULL,
                               end_date DATE,
                               start_odometer NUMERIC(10,2),
                               end_odometer NUMERIC(10,2),
                               purpose TEXT,
                               data_entered_by INT REFERENCES users(user_id),
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Servis kayıtları
CREATE TABLE services (
                          service_id SERIAL PRIMARY KEY,
                          vehicle_id INT REFERENCES vehicles(vehicle_id),
                          service_date DATE NOT NULL,
                          service_type service_type_enum NOT NULL,
                          description TEXT,
                          cost NUMERIC(10,2),
                          is_covered_by_insurance BOOLEAN DEFAULT FALSE,
                          data_entered_by INT REFERENCES users(user_id),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Yakıt tüketimi
CREATE TABLE fuel_consumption (
                                  fuel_id SERIAL PRIMARY KEY,
                                  vehicle_id INT REFERENCES vehicles(vehicle_id),
                                  driver_id INT REFERENCES drivers(driver_id),
                                  date DATE NOT NULL,
                                  liters NUMERIC(6,2),
                                  cost NUMERIC(10,2),
                                  odometer_reading NUMERIC(10,2),
                                  data_entered_by INT REFERENCES users(user_id),
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Şirketlerin gider takibi
CREATE TABLE expense_tracker (
                                 id SERIAL PRIMARY KEY,
                                 company_id INT REFERENCES companies(company_id),
                                 expense_type expense_type_enum NOT NULL
    -- rapor ve hesaplama fonksiyonları uygulama tarafında olacak
);

-- Her ay araçların kilometre değerlerinin loglanması için tablo
CREATE TABLE vehicle_odometer_log (
                                      id SERIAL PRIMARY KEY,
                                      vehicle_id INT REFERENCES vehicles(vehicle_id),
                                      log_month DATE NOT NULL,
                                      odometer_value NUMERIC(10,2) NOT NULL,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      UNIQUE(vehicle_id, log_month)
);


-- COMPANIES
INSERT INTO companies (company_id, name, address, phone_number, tax_number) VALUES (1, 'Smith, Bender and Weiss', '4816 Bass Knolls\nDavidsonmouth, NC 47085', '756-505-9096x01294', '1560841073377');
INSERT INTO companies (company_id, name, address, phone_number, tax_number) VALUES (2, 'Weiss PLC', '098 Curtis Cliffs Suite 313\nHowellburgh, NV 69476', '(201)724-6277x353', '7688123569418');
INSERT INTO companies (company_id, name, address, phone_number, tax_number) VALUES (3, 'Murphy-Mitchell', '8641 Rodgers Pines Apt. 385\nLake Robinland, AK 83238', '5289290908', '7236176106595');
INSERT INTO companies (company_id, name, address, phone_number, tax_number) VALUES (4, 'Garcia, Klein and Russell', '887 Browning Isle\nLake Hannah, KY 54354', '(639)088-6468x5898', '6828477133557');
INSERT INTO companies (company_id, name, address, phone_number, tax_number) VALUES (5, 'Howard-Cook', 'Unit 4137 Box 0852\nDPO AE 70871', '3549889742', '3851606937118');

-- USERS
INSERT INTO users (user_id, username, password, email, role, company_id) VALUES (1, 'timothydeleon', 'D7NNBWVg$Y', 'gsanders@yahoo.com', 'DRIVER', 4);
INSERT INTO users (user_id, username, password, email, role, company_id) VALUES (2, 'victor06', '8%1COqWbpN', 'kimberlyperry@golden-lozano.com', 'OUTSOURCE_USER', 3);
INSERT INTO users (user_id, username, password, email, role, company_id) VALUES (3, 'oliviavasquez', 'h2yRlCJ)@v', 'srogers@gmail.com', 'ADMIN', 3);
INSERT INTO users (user_id, username, password, email, role, company_id) VALUES (4, 'amiller', '!TvBFw(Z#4', 'dennisfernandez@murphy-simmons.biz', 'DRIVER', 4);
INSERT INTO users (user_id, username, password, email, role, company_id) VALUES (5, 'diana57', '^E#$JWdg2r', 'justin88@wood-mason.com', 'COMPANY_USER', 1);
INSERT INTO users (user_id, username, password, email, role, company_id) VALUES (6, 'hlittle', 'Q(7RLckzYB', 'laura81@gmail.com', 'ADMIN', 1);
INSERT INTO users (user_id, username, password, email, role, company_id) VALUES (7, 'tcook', 'CH9T9NmK^@', 'singletonjessica@carlson.com', 'COMPANY_USER', 3);
INSERT INTO users (user_id, username, password, email, role, company_id) VALUES (8, 'dianaadams', 'Ji2IpsgtS@', 'jennifergalvan@yahoo.com', 'OUTSOURCE_USER', 4);
INSERT INTO users (user_id, username, password, email, role, company_id) VALUES (9, 'tanya47', 'l^7rNqXcf1', 'darylweber@elliott.org', 'COMPANY_USER', 2);
INSERT INTO users (user_id, username, password, email, role, company_id) VALUES (10, 'adrienneharris', '5SSTs8PbJ#', 'gomezgeorge@gomez.com', 'OUTSOURCE_USER', 3);

-- VEHICLES
INSERT INTO vehicles (vehicle_id, company_id, plate_number, brand, model, year, type, status, ownership_type, current_odometer, previous_month_odometer, is_active) VALUES (1, 1, 'HDR-432', 'Kennedy, House and Taylor', 'page', 2015, 'SUV', 'WAITING_FOR_ASSIGNMENT', 'OWNED', 120825.46, 170220.03, TRUE);
INSERT INTO vehicles (vehicle_id, company_id, plate_number, brand, model, year, type, status, ownership_type, current_odometer, previous_month_odometer, is_active) VALUES (2, 5, '0-K2719', 'Whitney PLC', 'fear', 2020, 'VAN', 'WAITING_FOR_ASSIGNMENT', 'OWNED', 27728.11, 28270.43, True);
INSERT INTO vehicles (vehicle_id, company_id, plate_number, brand, model, year, type, status, ownership_type, current_odometer, previous_month_odometer, is_active) VALUES (3, 3, '4FZ 239', 'Tate PLC', 'could', 2014, 'SUV', 'OUT_OF_ORDER', 'OWNED', 183802.33, 92708.47, True);
INSERT INTO vehicles (vehicle_id, company_id, plate_number, brand, model, year, type, status, ownership_type, current_odometer, previous_month_odometer, is_active) VALUES (4, 1, '12O UA7', 'Deleon, Martin and Nguyen', 'difficult', 2011, 'VAN', 'ASSIGNED', 'LEASED', 156370.56, 164471.14, False);
INSERT INTO vehicles (vehicle_id, company_id, plate_number, brand, model, year, type, status, ownership_type, current_odometer, previous_month_odometer, is_active) VALUES (5, 2, 'H86 9TM', 'Long, Brooks and Gomez', 'best', 2024, 'VAN', 'IN_SERVICE', 'LEASED', 18426.79, 36825.55, True);
INSERT INTO vehicles (vehicle_id, company_id, plate_number, brand, model, year, type, status, ownership_type, current_odometer, previous_month_odometer, is_active) VALUES (6, 3, 'POI 0185', 'Bryant-Gallagher', 'operation', 2012, 'SUV', 'IN_SERVICE', 'OWNED', 194965.6, 126512.58, True);
INSERT INTO vehicles (vehicle_id, company_id, plate_number, brand, model, year, type, status, ownership_type, current_odometer, previous_month_odometer, is_active) VALUES (7, 1, 'R32 9BB', 'Rivera PLC', 'simply', 2021, 'SUV', 'OUT_OF_ORDER', 'LEASED', 87652.05, 144451.6, False);
INSERT INTO vehicles (vehicle_id, company_id, plate_number, brand, model, year, type, status, ownership_type, current_odometer, previous_month_odometer, is_active) VALUES (8, 2, '633 QEO', 'Richardson Ltd', 'read', 2022, 'TRUCK', 'ASSIGNED', 'LEASED', 162472.53, 40572.03, False);
INSERT INTO vehicles (vehicle_id, company_id, plate_number, brand, model, year, type, status, ownership_type, current_odometer, previous_month_odometer, is_active) VALUES (9, 1, '212IEY', 'Ross Ltd', 'son', 2019, 'VAN', 'AVAILABLE', 'OWNED', 106309.18, 137289.75, True);
INSERT INTO vehicles (vehicle_id, company_id, plate_number, brand, model, year, type, status, ownership_type, current_odometer, previous_month_odometer, is_active) VALUES (10, 3, '074 HZV', 'Gonzalez, Bryant and Matthews', 'type', 2023, 'SEDAN', 'ASSIGNED', 'OWNED', 188853.77, 83773.07, True);


-- DRIVERS
INSERT INTO drivers (driver_id, name, license_number, license_expiry_date, is_active) VALUES (1, 'Nicholas Reed', 'KQ378860', '2029-11-17', TRUE);
INSERT INTO drivers (driver_id, name, license_number, license_expiry_date, is_active) VALUES (2, 'Christopher Pugh', 'ns708525', '2027-11-29', True);
INSERT INTO drivers (driver_id, name, license_number, license_expiry_date, is_active) VALUES (3, 'Mr. Michael Perez', 'bS419931', '2032-08-28', False);
INSERT INTO drivers (driver_id, name, license_number, license_expiry_date, is_active) VALUES (4, 'Rachel Bradley', 'DE310545', '2031-10-18', False);
INSERT INTO drivers (driver_id, name, license_number, license_expiry_date, is_active) VALUES (5, 'Patricia Alexander', 'mz236490', '2032-12-13', False);
INSERT INTO drivers (driver_id, name, license_number, license_expiry_date, is_active) VALUES (6, 'Thomas Hill', 'iq314307', '2027-04-07', True);
INSERT INTO drivers (driver_id, name, license_number, license_expiry_date, is_active) VALUES (7, 'Steven Manning', 'TT655025', '2028-11-19', True);
INSERT INTO drivers (driver_id, name, license_number, license_expiry_date, is_active) VALUES (8, 'Justin Robinson', 'hI737236', '2032-01-09', False);
INSERT INTO drivers (driver_id, name, license_number, license_expiry_date, is_active) VALUES (9, 'Bobby Jones', 'AM398515', '2027-01-02', False);
INSERT INTO drivers (driver_id, name, license_number, license_expiry_date, is_active) VALUES (10, 'Jimmy Lewis', 'pB173363', '2033-03-05', True);

-- VEHICLE_USAGE
INSERT INTO vehicle_usage (usage_id, vehicle_id, driver_id, start_date, end_date, start_odometer, end_odometer, purpose, data_entered_by, created_at) VALUES
                                                                                                                                                          (1, 3, 7, '2025-03-08', '2025-04-26', 28357.79, 174338.81, 'Performance poor deal above low ever most.', 10, '2025-01-10 16:44:45'),
                                                                                                                                                          (2, 7, 5, '2025-04-02', '2025-04-24', 10599.01, 170642.09, 'Daughter night physical police surface.', 7, '2025-03-02 23:02:57'),
                                                                                                                                                          (3, 9, 10, '2025-04-02', '2025-04-08', 98959.9, 195485.54, 'Actually sign enter white public.', 1, '2025-03-05 18:18:04'),
                                                                                                                                                          (4, 5, 4, '2025-03-19', '2025-04-16', 63814.89, 186095.46, 'Per expect improve.', 2, '2025-04-07 05:29:23'),
                                                                                                                                                          (5, 1, 8, '2025-03-17', '2025-04-09', 127019.47, 172141.12, 'What watch simple walk image modern ask example.', 8, '2025-04-29 09:52:15'),
                                                                                                                                                          (6, 5, 2, '2025-03-04', '2025-04-04', 121121.95, 152110.82, 'Away culture though theory add card.', 6, '2025-01-04 22:19:22'),
                                                                                                                                                          (7, 4, 2, '2025-03-06', '2025-05-01', 32981.05, 176510.85, 'More agency also top interesting.', 8, '2025-03-08 18:04:51'),
                                                                                                                                                          (8, 7, 7, '2025-03-17', '2025-05-01', 127266.17, 162487.42, 'Watch energy agree.', 8, '2025-03-09 04:11:14'),
                                                                                                                                                          (9, 4, 6, '2025-03-09', '2025-04-19', 52335.34, 175086.2, 'Sell author rise wait.', 2, '2025-03-01 16:28:06'),
                                                                                                                                                          (10, 4, 3, '2025-03-20', '2025-04-09', 122405.68, 170555.72, 'Foreign short analysis sometimes country.', 4, '2025-04-05 13:26:13');

-- FUEL_CONSUMPTION
INSERT INTO fuel_consumption (fuel_id, vehicle_id, driver_id, date, liters, cost, odometer_reading, data_entered_by, created_at) VALUES
                                                                                                                                     (1, 3, 9, '2025-04-23', 69.28, 1185.29, 149202.89, 9, '2025-03-22 08:30:42'),
                                                                                                                                     (2, 7, 8, '2025-04-22', 30.71, 1222.55, 195066.78, 1, '2025-04-23 02:34:28'),
                                                                                                                                     (3, 5, 7, '2025-04-03', 71.32, 1214.06, 141352.92, 7, '2025-01-27 15:07:01'),
                                                                                                                                     (4, 7, 1, '2025-04-10', 23.38, 411.14, 27690.01, 9, '2025-02-08 15:35:41'),
                                                                                                                                     (5, 8, 6, '2025-04-10', 31.05, 1253.47, 112338.13, 5, '2025-02-22 13:48:14'),
                                                                                                                                     (6, 9, 7, '2025-04-11', 53.85, 1186.06, 46620.27, 4, '2025-03-23 14:21:32'),
                                                                                                                                     (7, 2, 5, '2025-04-10', 40.84, 634.13, 99342.65, 8, '2025-03-26 18:16:41'),
                                                                                                                                     (8, 2, 1, '2025-04-18', 22.1, 760.95, 147480.03, 5, '2025-01-18 02:53:54'),
                                                                                                                                     (9, 10, 2, '2025-04-22', 38.71, 1352.44, 18871.55, 4, '2025-01-20 22:08:19'),
                                                                                                                                     (10, 2, 4, '2025-04-11', 49.58, 603.38, 35697.16, 2, '2025-03-26 07:06:47');

-- SERVICES
INSERT INTO services (service_id, vehicle_id, service_date, service_type, description, cost, is_covered_by_insurance, data_entered_by, created_at) VALUES
                                                                                                                                                       (1, 8, '2025-02-26', 'PART_REPLACEMENT', 'Now such both history art child down character peace into.', 2498.72, FALSE, 5, '2025-02-15 03:48:19'),
                                                                                                                                                       (2, 7, '2025-04-13', 'TIRE_CHANGE', 'Hundred important policy home may close first nice travel.', 4968.07, TRUE, 1, '2025-03-18 06:50:31'),
                                                                                                                                                       (3, 10, '2025-03-11', 'REPAIR', 'American ground church center bring Congress successful any represent.', 1212.73, FALSE, 4, '2025-01-03 23:33:12'),
                                                                                                                                                       (4, 2, '2025-02-07', 'REGULAR_MAINTENANCE', 'Production serious matter here talk town option treatment apply.', 541.71, TRUE, 8, '2025-02-14 06:25:53'),
                                                                                                                                                       (5, 1, '2025-04-07', 'PART_REPLACEMENT', 'Together player peace couple recognize avoid enter performance lawyer south.', 826.17, FALSE, 4, '2025-01-15 07:27:58'),
                                                                                                                                                       (6, 10, '2025-04-29', 'REGULAR_MAINTENANCE', 'Data near ago walk leave respond six floor.', 1813.6, TRUE, 5, '2025-04-22 00:40:29'),
                                                                                                                                                       (7, 9, '2025-04-23', 'PART_REPLACEMENT', 'Key again event moment bill major available father lawyer argue.', 1905.28, TRUE, 10, '2025-03-22 03:11:30'),
                                                                                                                                                       (8, 7, '2025-02-15', 'REPAIR', 'Own plant threat front option itself agree community.', 1628.61, FALSE, 5, '2025-03-10 19:34:41'),
                                                                                                                                                       (9, 7, '2025-03-07', 'TIRE_CHANGE', 'Sort believe air difficult dream such.', 886.03, FALSE, 1, '2025-04-04 10:30:45'),
                                                                                                                                                       (10, 9, '2025-04-05', 'TIRE_CHANGE', 'Perhaps act western religious something win owner.', 3120.19, FALSE, 8, '2025-03-27 02:28:06');

-- VEHICLE_ODOMETER_LOG
INSERT INTO vehicle_odometer_log (id, vehicle_id, log_month, odometer_value, created_at) VALUES
                                                                                             (1, 1, '2025-01-01', 93959.46, '2025-03-10 00:41:15'),
                                                                                             (2, 8, '2024-11-01', 161813.34, '2025-01-24 22:45:56'),
                                                                                             (3, 6, '2024-12-01', 145571.08, '2025-04-02 15:38:10'),
                                                                                             (4, 6, '2024-11-01', 164601.23, '2025-01-25 03:06:56'),
                                                                                             (5, 9, '2024-12-01', 162565.70, '2025-04-01 21:42:31'),
                                                                                             (6, 8, '2025-01-01', 175691.57, '2025-04-01 03:36:38'),
                                                                                             (7, 8, '2025-02-01', 57528.76, '2025-02-23 15:44:30'),
                                                                                             (8, 9, '2025-04-01', 43536.37, '2025-04-23 23:11:37'),
                                                                                             (9, 2, '2025-01-01', 33062.31, '2025-04-27 01:44:29'),
                                                                                             (10, 5, '2024-12-01', 27882.19, '2025-04-06 03:25:36');

-- EXPENSE_TRACKER
INSERT INTO expense_tracker (id, company_id, expense_type) VALUES
                                                               (1, 1, 'FUEL'),
                                                               (2, 1, 'FUEL'),
                                                               (3, 1, 'FUEL'),
                                                               (4, 4, 'FUEL'),
                                                               (5, 3, 'TOTAL'),
                                                               (6, 3, 'TOTAL'),
                                                               (7, 2, 'REPAIR'),
                                                               (8, 4, 'FUEL'),
                                                               (9, 1, 'REPAIR'),
                                                               (10, 4, 'FUEL');
