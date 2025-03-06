package com.azathoth.CatmonMalabonHealthCenterSystem.controller;

import com.azathoth.CatmonMalabonHealthCenterSystem.dto.DoctorAuthenticationDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.dto.PatientDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.exception.ResourceNotFoundException;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.PatientRecord;
import com.azathoth.CatmonMalabonHealthCenterSystem.service.DoctorService;
import com.azathoth.CatmonMalabonHealthCenterSystem.utils.Status;
import jakarta.validation.Valid;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

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
     * Get all my patients
     * Signed in doctor should pass his id here
     */
    @GetMapping("/private/get-all-my-patients/{myId}")
    public ResponseEntity<?> getAllMyPatients(@PathVariable Long myId) {
        try {
            List<PatientDTO> allMyPatients = doctorService.getAllMyPatients(myId);

            return allMyPatients.isEmpty() ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.ok().body(allMyPatients);
        }
        catch (ResourceNotFoundException e) {
            logger.error("Doctor not found by id: {}", myId, e);
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("Error fetching patients by id: {}", myId, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }

    /**
     * Update patient status
     * patient id should be from getAllMyPatients() method
     */
    @PutMapping("/private/update-patient-status/{patientId}/{newStatus}")
    public ResponseEntity<?> updatePatientStatus(@PathVariable Long patientId, @PathVariable Status newStatus) {
        try {
            Optional<PatientDTO> updatedPatient = doctorService.updatePatientStatus(patientId, newStatus);

            if(updatedPatient.isPresent()) {
                // Broadcast the updated patient status to all WebSocket /patients subscribers
                messagingTemplate.convertAndSend("/topic/patients", updatedPatient.get());

                // return the updated patient
                return ResponseEntity.ok().body(updatedPatient);
            }

            // return not found response if there's no patient matching by patientId
            return  ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("Error occurred updating patient status: {}", e.getMessage());
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
}
