package com.azathoth.CatmonMalabonHealthCenter.controller;

import com.azathoth.CatmonMalabonHealthCenter.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenter.model.PatientRecord;
import com.azathoth.CatmonMalabonHealthCenter.model.Status;
import com.azathoth.CatmonMalabonHealthCenter.model.utils.PatientDTO;
import com.azathoth.CatmonMalabonHealthCenter.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {
    // stores error and good message as object to response as json
    private final Map<String, String> errorMessage = new HashMap<>();
    private final Map<String, String> goodMessage = new HashMap<>();

    private final DoctorService doctorService;
    // for websocket enabling controller to subscribe
    private final SimpMessagingTemplate messagingTemplate;

    public DoctorController(DoctorService doctorService, SimpMessagingTemplate messagingTemplate) {
        this.doctorService = doctorService;
        this.messagingTemplate = messagingTemplate;

        errorMessage.put("error", "");
        goodMessage.put("message", "");
    }

    /**
     * Doctor controller endpoint.
     * Doctor should pass request object with value: completeName,
     * email, password, and an array of availableDay enum
     */
    @PostMapping("/public/register")
    public ResponseEntity<?> register(@RequestBody Doctor newDoctor) {
        try {
            // check for empty or null fields to avoid sql injection
            if(newDoctor.getCompleteName().trim().isEmpty() || newDoctor.getCompleteName() == null ||
                newDoctor.getEmail().trim().isEmpty() || newDoctor.getEmail() == null ||
                newDoctor.getPassword().trim().isEmpty() || newDoctor.getPassword() == null ||
                newDoctor.getAvailableDays() == null
            ) {
                errorMessage.replace("error", "Input fields cannot be empty");
                return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
            }

            // register the new doctor
            Optional<Doctor> addedNewDoctor = doctorService.register(newDoctor);
            if(addedNewDoctor.isPresent()) {
                // Broadcast the new doctor to all WebSocket subscribers
                messagingTemplate.convertAndSend("/topic/users", addedNewDoctor.get());
                goodMessage.replace("message", "Successful registration");
                return new ResponseEntity<>(goodMessage, HttpStatus.CREATED);
            }

            errorMessage.replace("error", "Invalid registration");
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid registration");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * authenticate doctor credentials
     */
    @PostMapping("/public/login")
    public ResponseEntity<?> authenticate(@RequestBody Doctor doctor) {
        try {
            if(doctor.getEmail().trim().isEmpty() || doctor.getEmail() == null ||
                doctor.getPassword().trim().isEmpty() || doctor.getPassword() == null
            ) {
                errorMessage.replace("error", "Invalid email or password");
                return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
            }

            // verify the doctor
            Optional<?> verifyDoctor = doctorService.authenticate(doctor);

            goodMessage.replace("message", "Login successful");
            errorMessage.replace("error", "Invalid email or password");

            return  verifyDoctor.isEmpty() ?
                    new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED) :
                    new ResponseEntity<>(verifyDoctor, HttpStatus.OK);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid registration");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
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

            errorMessage.replace("error", "No content");

            return allMyPatients.isEmpty() ?
                    new ResponseEntity<>(errorMessage, HttpStatus.NO_CONTENT) :
                    new ResponseEntity<>(allMyPatients, HttpStatus.OK);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid request");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
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
                // Broadcast the updated patient status to all WebSocket subscribers
                messagingTemplate.convertAndSend("/topic/users", updatedPatient.get());
                return new ResponseEntity<>(updatedPatient, HttpStatus.OK);
            }

            errorMessage.replace("error", "Status is not available");
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid request");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
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
                messagingTemplate.convertAndSend("/topic/users", patientRecord.get());
                return new ResponseEntity<>(patientRecord, HttpStatus.CREATED);
            }

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid request");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
