package com.azathoth.CatmonMalabonHealthCenter.controller;

import com.azathoth.CatmonMalabonHealthCenter.model.Admin;
import com.azathoth.CatmonMalabonHealthCenter.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenter.model.Patient;
import com.azathoth.CatmonMalabonHealthCenter.model.utils.DoctorDTO;
import com.azathoth.CatmonMalabonHealthCenter.model.utils.PatientDTO;
import com.azathoth.CatmonMalabonHealthCenter.model.utils.UpdateDoctor;
import com.azathoth.CatmonMalabonHealthCenter.model.utils.UpdatePatient;
import com.azathoth.CatmonMalabonHealthCenter.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Controller
@RequestMapping("/api/admin")
public class AdminController {
    // stores error and good message as object to response as json
    private final Map<String, String> errorMessage = new HashMap<>();
    private final Map<String, String> goodMessage = new HashMap<>();

    private final AdminService adminService;
    // for websocket enabling controller to subscribe
    private final SimpMessagingTemplate messagingTemplate;

    public AdminController(AdminService adminService, SimpMessagingTemplate messagingTemplate) {
        this.adminService = adminService;
        this.messagingTemplate = messagingTemplate;

        errorMessage.put("error", "");
        goodMessage.put("message", "");
    }

    /**
     * create account for admin
     * required fields for the body: adminName, email and password
     */
    @PostMapping("/public/create")
    public ResponseEntity<?> createAccount(@RequestBody Admin admin) {
        try {
            if(admin.getAdminName().trim().isEmpty() || admin.getAdminName() == null ||
                admin.getEmail().trim().isEmpty() || admin.getEmail() == null ||
                admin.getPassword().trim().isEmpty() || admin.getPassword() == null
            ) {
                errorMessage.replace("error", "Input fields cannot be empty");
                return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
            }

            // create admin acc
            Optional<Admin> createdAdmin = adminService.createAccount(admin);

            if(createdAdmin.isPresent()) {
                // Broadcast the new admin to all WebSocket subscribers
                messagingTemplate.convertAndSend("/topic/users", createdAdmin.get());
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
     * authenticate admin credentials
     */
    @PostMapping("/public/auth")
    public ResponseEntity<?> authenticate(@RequestBody Admin admin) {
        try {
            if(admin.getEmail().trim().isEmpty() || admin.getEmail() == null ||
                admin.getPassword().trim().isEmpty() || admin.getPassword() == null
            ) {
                errorMessage.replace("error", "Invalid email or password");
                return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
            }

            Optional<?> authenticateAdmin = adminService.authenticate(admin);

            errorMessage.replace("error", "Invalid email or password");

            return  authenticateAdmin.isEmpty() ?
                    new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED) :
                    new ResponseEntity<>(authenticateAdmin, HttpStatus.OK);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid registration");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Uses a non-entity class for updating a doctor
     * completeName, password, availableDays are updatable and not
     * included the email because of db constraints. So in the frontend,
     * the existing email will automatically include to pass in request body.
     */
    @PutMapping("/private/update-doctor")
    public ResponseEntity<?> updateDoctor(@RequestBody UpdateDoctor doctor) {
        try {
            // check for empty or null fields to avoid sql injection
            if(doctor.getNewCompleteName().trim().isEmpty() || doctor.getNewCompleteName() == null ||
                doctor.getExistingEmail().trim().isEmpty() || doctor.getExistingEmail() == null ||
                doctor.getNewPassword().trim().isEmpty() || doctor.getNewPassword() == null ||
                doctor.getNewAvailableDays() == null
            ) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Optional<Doctor> updatedDoctor = adminService.updateDoctor(doctor);

            if(updatedDoctor.isPresent()) {
                // Broadcast the updated doctor to all WebSocket subscribers
                messagingTemplate.convertAndSend("/topic/users", updatedDoctor.get());
                goodMessage.replace("message", "Update successful");
                return new ResponseEntity<>(goodMessage, HttpStatus.OK);
            }

            errorMessage.replace("error", "Update failed");
            return new ResponseEntity<>(errorMessage, HttpStatus.NOT_ACCEPTABLE);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid registration");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/private/delete-doctor/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        try {
            if(id == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            boolean deletedDoctor =  adminService.deleteDoctor(id);

            // get all doctors to pass to broadcast to all websocket endpoint subscribers
            List<DoctorDTO> getAllDoctor = adminService.getAllDoctors();

            if(!getAllDoctor.isEmpty()) {
                messagingTemplate.convertAndSend("/topic/users", getAllDoctor);
                return new ResponseEntity<>(HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid request");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * SEARCH Doctor
     */
    @GetMapping("/private/search-doctor/{keyword}")
    public ResponseEntity<?> searchDoctor(@RequestParam String keyword) {
        List<DoctorDTO> searchDoctor = adminService.searchDoctor(keyword);

        return new ResponseEntity<>(searchDoctor, HttpStatus.OK);
    }

    /**
     * Get all doctors
     */
    @GetMapping("/private/get-all-doctors")
    public ResponseEntity<?> getAllDoctors() {
        try {
            List<DoctorDTO> allDoctors = adminService.getAllDoctors();

            return allDoctors.isEmpty() ?
                    new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                    new ResponseEntity<>(allDoctors, HttpStatus.OK);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid registration");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update patient
     * ID is important because it is unique in all patient table row.
     * Update will be based on existing id so id cannot and should not be updated.
     */
    @PutMapping("/private/update-patient")
    public ResponseEntity<?> updatePatient(@RequestBody UpdatePatient patient) {
        try {
            if(patient.getNewCompleteName().trim().isEmpty() || patient.getNewCompleteName() == null ||
                patient.getNewGender().trim().isEmpty() || patient.getNewGender() == null ||
                patient.getNewAddress().trim().isEmpty() || patient.getNewAddress() == null ||
                patient.getNewAge() == 0 || patient.getId() == null || patient.getId() == 0
            ) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Optional<Patient> updatedPatient = adminService.updatePatient(patient);
            if(updatedPatient.isPresent()) {
                // Broadcast the updated patient to all WebSocket subscribers
                messagingTemplate.convertAndSend("/topic/users", updatedPatient.get());
                goodMessage.replace("message", "Update successful");
                return new ResponseEntity<>(goodMessage, HttpStatus.OK);
            }

            errorMessage.replace("error", "Update failed");

            return new ResponseEntity<>(errorMessage, HttpStatus.NOT_ACCEPTABLE);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid request");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete patient
     */
    @DeleteMapping("/private/delete-patient/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Long id) {
        try {
            if(id == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            boolean deletedPatient =  adminService.deletePatient(id);
            List<PatientDTO> allPatients = adminService.getAllPatients();
            if(deletedPatient) {
                // Broadcast the deleted patient to all WebSocket subscribers
                messagingTemplate.convertAndSend("/topic/users", allPatients);
                return new ResponseEntity<>(HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid request");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search Patient
     */
    @GetMapping("/private/search-patient/{keyword}")
    public ResponseEntity<?> searchPatient(@RequestParam String keyword) {
        List<PatientDTO> searchPatient = adminService.searchPatient(keyword);

        return new ResponseEntity<>(searchPatient, HttpStatus.OK);
    }

    /**
     * GET all patients
     */
    @GetMapping("/private/get-all-patients")
    public ResponseEntity<?> getAllPatient() {
        try {
            List<PatientDTO> allPatients = adminService.getAllPatients();

            return allPatients.isEmpty() ?
                    new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                    new ResponseEntity<>(allPatients, HttpStatus.OK);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid request");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
