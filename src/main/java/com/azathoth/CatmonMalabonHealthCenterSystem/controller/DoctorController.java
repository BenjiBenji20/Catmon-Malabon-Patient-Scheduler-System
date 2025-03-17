package com.azathoth.CatmonMalabonHealthCenterSystem.controller;

import com.azathoth.CatmonMalabonHealthCenterSystem.dto.DoctorAuthenticationDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.dto.DoctorDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.dto.PatientDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.exception.ResourceNotFoundException;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.PatientRecord;
import com.azathoth.CatmonMalabonHealthCenterSystem.service.DoctorService;
import com.azathoth.CatmonMalabonHealthCenterSystem.utils.AvailableDay;
import com.azathoth.CatmonMalabonHealthCenterSystem.utils.Status;
import jakarta.validation.Valid;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    private final DoctorService doctorService;

    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);

    public DoctorController(DoctorService doctorService, SimpMessagingTemplate messagingTemplate) {
        this.doctorService = doctorService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * authenticate doctor credentials
     */
    @PostMapping("/public/login")
    public ResponseEntity<?> authenticateDoctor(@Valid @RequestBody DoctorAuthenticationDTO doctorDto) {
        try {
            Optional<?> authenticateDoctor = doctorService.authenticateDoctor(doctorDto);

            if(authenticateDoctor.isPresent()) {
                return ResponseEntity.ok().body(authenticateDoctor);
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Invalid email or password"));
        }
        catch (DataException dataException) {
            logger.error("Doctor does not exist by id: {}", dataException.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Error database"));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }

    /**
     * GET doctor profile (get the current profile of logged doctor)
     * used the header to extract doctor data using jwt token.
     */
    @GetMapping("/private/get-doctor-profile")
    public ResponseEntity<?> getDoctorProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from header
            String token = authHeader.replace("Bearer ", "");

            // extract doctor profile
            Optional<DoctorDTO> doctor = doctorService.getDoctorProfile(token);

            return doctor.isEmpty() ?
                    ResponseEntity.notFound().build() :
                    ResponseEntity.ok(doctor);
        }
        catch (ResourceNotFoundException e) {
            logger.error("Doctor not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error while fetching data"));
        }
    }

    /**
     * Get all my patients
     * Signed in doctor should pass his id here
     */
    @GetMapping("/private/get-all-my-patients")
    public ResponseEntity<?> getAllMyPatients(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from header
            String token = authHeader.replace("Bearer ", "");

            // extract doctor profile
            DoctorDTO doctor = doctorService.getDoctorProfile(token)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found. Invalid token: " + token));

            List<PatientDTO> allMyPatients = doctorService.getAllMyPatients(doctor.getId());

            return allMyPatients.isEmpty() ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.ok().body(allMyPatients);
        }
        catch (ResourceNotFoundException e) {
            logger.error("Doctor not found");
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("Error fetching patients");
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }

    /**
     * Get all patients based on the current date
     */
    @GetMapping("/private/get-patients-today/{date}")
    public ResponseEntity<?> getPatientsToday(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                              @RequestHeader("Authorization") String authHeader
                                              ) {
        try {
            // Extract token from header
            String token = authHeader.replace("Bearer ", "");

            // extract doctor profile
            DoctorDTO doctor = doctorService.getDoctorProfile(token)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

            List<PatientDTO> patientsToday = doctorService.getPatientsToday(date, doctor.getId());

            return patientsToday.isEmpty() ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.ok().body(patientsToday);
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found by date: {}", date);
            return ResponseEntity.internalServerError().body(Map.of("error", "Not found by param date."));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }

    @GetMapping("/private/patients-by-available-days")
    public ResponseEntity<?> getPatientsByAvailableDays(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from header
            String token = authHeader.replace("Bearer ", "");

            // extract doctor profile
            Optional<DoctorDTO> doctor = doctorService.getDoctorProfile(token);

            if(doctor.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Map<AvailableDay, List<PatientDTO>> patients = doctorService.getPatientsByAvailableDays(doctor.get());

            System.out.println(patients);

            return patients.isEmpty() ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.ok(patients);
        }
        catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error while fetching data"));
        }
    }

    /**
     * Get patient details by id
     */
    @GetMapping("/private/get-patient-details/{id}")
    public ResponseEntity<?> getPatientDetails(@PathVariable Long id) {
        try {
            Optional<PatientDTO> patient = doctorService.getPatientDetails(id);

            return patient.isEmpty() ?
                    ResponseEntity.notFound().build() :
                    ResponseEntity.ok(patient);
        }
        catch (ResourceNotFoundException e) {
            logger.error("Patient not found by id: {}", id);
            return ResponseEntity.notFound().build();
        }
        catch (DataAccessException e) {
            logger.error("Data cannot be fetch: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Error occurred while fetching data."));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error found"));
        }
    }

    /**
     * Update patient status
     * patient id should be from getAllMyPatients() method
     */
    @PutMapping("/private/update-patient-status")
    public ResponseEntity<?> updatePatientStatus(
            @RequestParam Long patientId,
            @RequestParam Status newStatus) {
        try {
            int updatedPatient = doctorService.updatePatientStatus(patientId, newStatus);

            return updatedPatient > 0 ?
                    ResponseEntity.ok().body(Map.of("message", "Rows updated successfully: " + updatedPatient)) :
                    // return not found response if there's no patient matching by patientId
                    ResponseEntity.notFound().build();
        }
        catch (IllegalArgumentException e) {
            logger.error("Invalid status value: {}", newStatus, e);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status value: " + newStatus));
        }
        catch (IllegalStateException e) {
            logger.error("Cannot update status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error updating status for patient {} to {}: {}", patientId, newStatus, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }

    /**
     * Create a patient record
     */
    @PostMapping("/private/patient-record/{patientId}")
    public ResponseEntity<?> createPatientRecord(@PathVariable Long patientId, @RequestBody PatientRecord record) {
        try {
            Optional<PatientRecord> patientRecord = doctorService.createPatientRecord(patientId, record);

            if(patientRecord.isPresent()) {
                // Broadcast the new patient record to all WebSocket /records subscribers
                messagingTemplate.convertAndSend("/topic/records", patientRecord.get());
                return ResponseEntity.ok().body(patientRecord);
            }

            // return not found response if there's no patient matching by patientId
            return  ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("An error occurred creating patient record: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "An error occurred creating patient record"));
        }
    }

    /**
     * Filter table
     * @RequestParam(required = false) makes the
     * parameter binding flexible by not requiring each
     * parameter to have value.
     */
    @GetMapping("/private/filter")
    public ResponseEntity<?> filterPatient(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String status
    ) {
        try {
            // Extract token from header
            String token = authHeader.replace("Bearer ", "");

            // extract doctor profile
            DoctorDTO doctor = doctorService.getDoctorProfile(token)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found. Invalid token: " + token));

            Optional<List<PatientDTO>> filterPatient = doctorService.filterPatient(doctor.getId(), gender, age, status);

            return filterPatient.isEmpty() ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.ok().body(filterPatient);
        }
        catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }

    /**
     * Search Patient
     */
    @GetMapping("/private/search-patient")
    public ResponseEntity<?> searchPatient(@RequestHeader("Authorization") String authHeader, @RequestParam String keyword) {
        // Extract token from header
        String token = authHeader.replace("Bearer ", "");

        // extract doctor profile
        DoctorDTO doctor = doctorService.getDoctorProfile(token)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found. Invalid token: " + token));

        List<PatientDTO> searchPatient = doctorService.searchPatient(doctor.getId(), keyword);

        return new ResponseEntity<>(searchPatient, HttpStatus.OK);
    }
}
