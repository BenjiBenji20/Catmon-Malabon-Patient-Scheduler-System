package com.azathoth.CatmonMalabonHealthCenterSystem.repository;

import com.azathoth.CatmonMalabonHealthCenterSystem.model.PatientRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRecordRepository extends JpaRepository<PatientRecord, Long> {
    @Query("SELECT p FROM PatientRecord p WHERE p.appointment.id = :appointmentId")
    PatientRecord findAppointmentById(@Param("appointmentId") Long appointmentId);

    // updating patient record to avoid db constraints for duplicate appointment id
    @Modifying
    @Query("UPDATE PatientRecord p SET p.isAttended = :isAttended, p.diagnosis = :diagnosis, " +
            "p.prescription = :prescription WHERE p.appointment.id = :appointmentId")
    int updatePatientRecord(Long appointmentId, boolean isAttended, String diagnosis, String prescription);
}
