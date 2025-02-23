package com.azathoth.CatmonMalabonHealthCenter.repository;

import com.azathoth.CatmonMalabonHealthCenter.model.PatientRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRecordRepository extends JpaRepository<PatientRecord, Long> {
    PatientRecord findAppointmentById(Long appointmentId);
}
