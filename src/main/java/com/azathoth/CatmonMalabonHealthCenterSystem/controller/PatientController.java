package com.azathoth.CatmonMalabonHealthCenterSystem.controller;

import com.azathoth.CatmonMalabonHealthCenterSystem.model.Patient;
import com.azathoth.CatmonMalabonHealthCenterSystem.service.AppointmentService;
import com.azathoth.CatmonMalabonHealthCenterSystem.service.PatientService;
import jakarta.validation.Valid;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    public PatientController(AppointmentService appointmentService, PatientService patientService, SimpMessagingTemplate messagingTemplate) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate
     * used to specify the format of a date when binding a request parameter
     * LocalDate schedule represents a date (without time) in the format YYYY-MM-DD.
     * @param schedule
     * @return boolean if date is available
     */
    @PostMapping("/available-schedule/{schedule}")
    public ResponseEntity<?> availableSchedule(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate schedule) {
        try {
            // store the result in boolean value
            boolean isAvailableSchedule = appointmentService.availableSchedule(schedule);

            return isAvailableSchedule ?
                    ResponseEntity.ok().body(Map.of("message", "Schedule is available")) :
                    ResponseEntity.badRequest().body(Map.of("error", "Schedule is not available"));
        }
        catch (Exception e) {
            logger.error("An error occurred: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Server cannot response"));
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody Patient newPatient) {
        try {
            // check for the availability of schedule
            LocalDate schedule = newPatient.getAppointment().getScheduleDate();
            boolean isAvailableSchedule = appointmentService.availableSchedule(schedule);

            // if schedule is not available
            if(!isAvailableSchedule) {
                return ResponseEntity.badRequest().body(Map.of("error", "Schedule is not available"));
            }

            // pass new patient to service to validate it and save to the db
            Optional<Patient> addedNewPatient = patientService.registerPatient(newPatient);

            if(addedNewPatient.isPresent()) {
                // Broadcast the new doctor to all WebSocket /patients subscribers
                messagingTemplate.convertAndSend("/topic/patients", addedNewPatient.get());
                return ResponseEntity.ok().body(Map.of("message", "Registered successfully"));
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Registration failed. Please try again."));
        }
        catch (DataException d) {
            logger.error("Database isn't available: {}", d.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Server cannot response"));
        }
    }


}
