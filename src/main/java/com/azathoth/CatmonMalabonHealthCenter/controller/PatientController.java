package com.azathoth.CatmonMalabonHealthCenter.controller;

import com.azathoth.CatmonMalabonHealthCenter.model.Patient;
import com.azathoth.CatmonMalabonHealthCenter.service.AppointmentService;
import com.azathoth.CatmonMalabonHealthCenter.service.PatientService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RestController
@RequestMapping("/api/patient")
public class PatientController {

    // dependencies injected via constructor
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    // stores error and good message as object to response as json
    private final Map<String, String> errorMessage = new HashMap<>();
    private final Map<String, String> goodMessage = new HashMap<>();

    public PatientController(PatientService patientService, AppointmentService appointmentService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;

        errorMessage.put("error", "");
        goodMessage.put("message", "");
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

            goodMessage.replace("message", "Schedule is available");
            errorMessage.replace("error", "Schedule is not available");

            return isAvailableSchedule ?
                    ResponseEntity.ok(goodMessage) :
                    ResponseEntity.badRequest().body(errorMessage);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid registration");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@RequestBody Patient newPatient) {
        try {
            // validation for empty fields
            // Validate input fields
            if (newPatient.getCompleteName() == null || newPatient.getCompleteName().trim().isEmpty() ||
                    newPatient.getAddress() == null || newPatient.getAddress().trim().isEmpty() ||
                    newPatient.getGender() == null || newPatient.getGender().trim().isEmpty() ||
                    newPatient.getContactNumber() == null || newPatient.getContactNumber().trim().isEmpty() ||
                    newPatient.getAge() <= 0) {
                errorMessage.replace("error", "Input fields cannot be empty");
                return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
            }

            // check for the availability of schedule
            LocalDate schedule = newPatient.getAppointment().getScheduleDate();
            boolean isAvailableSchedule = appointmentService.availableSchedule(schedule);

            // if schedule is not available
            if(!isAvailableSchedule) {
                errorMessage.replace("error", "Schedule is not available");
                return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
            }

            // pass new patient to service to validate it and save to the db
            Optional<Patient> addedNewPatient = patientService.registerPatient(newPatient);

            goodMessage.replace("message", "Successful registration");
            errorMessage.replace("error", "Invalid registration");

            return addedNewPatient.isEmpty() ?
                    new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST) :
                    new ResponseEntity<>(goodMessage, HttpStatus.CREATED);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid registration");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
