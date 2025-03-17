package com.azathoth.CatmonMalabonHealthCenterSystem.repository;

import com.azathoth.CatmonMalabonHealthCenterSystem.model.Appointment;
import com.azathoth.CatmonMalabonHealthCenterSystem.utils.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    long countByScheduleDate(LocalDate schedule);

    // Get all appointments for a specific doctor
    List<Appointment> findByDoctorId(Long doctorId);

    // query appointments by current date and doctor's id
    @Query("SELECT a FROM Appointment a WHERE a.scheduleDate = :date AND a.doctor.id = :doctorId")
    List<Appointment> findAppointmentsByDateAndDoctor(@Param("date") LocalDate date, @Param("doctorId") Long doctorId);

    // update only patient status
    @Modifying
    @Query("UPDATE Appointment a SET a.status = :newStatus WHERE a.id = :appointmentId")
    int updatePatientStatus(Long appointmentId, Status newStatus);
}
