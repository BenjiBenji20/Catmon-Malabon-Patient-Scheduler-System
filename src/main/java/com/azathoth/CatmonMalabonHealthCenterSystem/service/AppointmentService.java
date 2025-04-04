package com.azathoth.CatmonMalabonHealthCenterSystem.service;

import com.azathoth.CatmonMalabonHealthCenterSystem.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AppointmentService {
    // dependencies injected via constructor
    private final AppointmentRepository appointmentRepository;

    // max patient schedule per date
    private final static long MAX_PATIENT_SCHEDULE_PER_DATE = 50;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public boolean availableSchedule(LocalDate schedule) {
        // compare date today vs passed date
        LocalDate today = LocalDate.now();
        boolean isDateFromPast = schedule.isBefore(today);
        if(isDateFromPast) {
            return false;
        }

        // check in the db using repository the patient count
        long patientCount = appointmentRepository.countByScheduleDate(schedule);

        // return true if patient count passed schedule is lesser than 50
        return patientCount < MAX_PATIENT_SCHEDULE_PER_DATE;
    }
}
