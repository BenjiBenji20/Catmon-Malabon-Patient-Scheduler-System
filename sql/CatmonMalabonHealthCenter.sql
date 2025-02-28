CREATE SCHEMA catmon_malabon_health_center;

USE catmon_malabon_health_center;

SHOW TABLES;

SELECT * FROM appointment;

SELECT * FROM patient_record;

SELECT * FROM patient;

SELECT * FROM doctor;

SELECT * FROM admin;

SELECT * FROM doctor_available_days;

-- for filtering patient table converted into jpa query
SELECT * 
FROM patient 
WHERE LOWER(patient.gender) = 'male' 
   AND patient.age = 30
   AND EXISTS (
       SELECT 1 
       FROM appointment 
       WHERE appointment.patient_id = patient.id 
         AND LOWER(appointment.patient_status) = 'ongoing'
   );

-- implementation #2 much flexible!
SELECT p.gender, 
	p.age, 
	a.patient_status 
FROM patient AS p 
INNER JOIN appointment AS a
WHERE p.gender = 'male' AND 
p.age = 30 AND 
a.patient_status = 'ongoing';

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

-- SET FOREIGN_KEY_CHECKS = 1;








