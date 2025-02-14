package com.azathoth.CatmonMalabonHealthCenter.repository;

import com.azathoth.CatmonMalabonHealthCenter.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    long countByScheduleDate(LocalDate schedule);
}
