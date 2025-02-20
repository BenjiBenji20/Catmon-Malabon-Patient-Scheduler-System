CREATE SCHEMA catmon_malabon_health_center;

USE catmon_malabon_health_center;

SHOW TABLES;

SELECT * FROM appointment;

SELECT * FROM patient_record;

SELECT * FROM patient;

SELECT * FROM doctor;

SELECT * FROM admin;

SELECT * FROM doctor_available_days;

-- SET FOREIGN_KEY_CHECKS = 0;

-- DROP TABLE appointment;
-- DROP TABLE patient;
-- DROP TABLE doctor;
-- DROP TABLE doctor_available_days;
-- DROP TABLE patient_record;
-- DROP TABLE admin;

-- SHOW create table appointment;
-- show create table doctor;
-- show create table patient;

-- alter table patient drop foreign key appointment_id;

SET FOREIGN_KEY_CHECKS = 1;








