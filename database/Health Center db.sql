CREATE SCHEMA catmon_malabon_health_center_db;

USE catmon_malabon_health_center_db;

SHOW TABLEs;

SELECT * FROM patient;

SELECT * FROM pending_doctor;

SELECT * FROM admin;

SELECT * FROM doctor;

SELECT * FROM patient_record;

SELECT * FROM doctor_available_days;

SELECT * FROM appointment;

SELECT * FROM patient 
	JOIN appointment a 
    WHERE a.schedule_date = 2025-03-09;