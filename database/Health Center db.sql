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

SELECT p.* 
FROM patient p
JOIN appointment a ON p.id = a.patient_id
WHERE a.schedule_date = '2025-03-09';

UPDATE appointment set patient_status = 'CANCELLED' WHERE patient_id = 13;

-- checking patient record constraints
SHOW CREATE TABLE patient_record;

DELETE FROM patient_record WHERE appointment_id = 5;