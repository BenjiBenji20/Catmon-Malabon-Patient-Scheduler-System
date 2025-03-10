package com.azathoth.CatmonMalabonHealthCenterSystem.controller;

import com.azathoth.CatmonMalabonHealthCenterSystem.dto.*;
import com.azathoth.CatmonMalabonHealthCenterSystem.exception.ResourceNotFoundException;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Admin;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.service.AdminService;
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
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final SimpMessagingTemplate messagingTemplate;

    public AdminController(AdminService adminService, SimpMessagingTemplate messagingTemplate) {
        this.adminService = adminService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * create account for admin
     * required fields for the body: adminName, email and password
     */
    @PostMapping("/public/create")
    public ResponseEntity<?> createAccount(@Valid @RequestBody AdminDTO admin) {
        try {
            Optional<Admin> newAdmin = adminService.createAccount(admin);

            if(newAdmin.isPresent()) {
                // Broadcast the new admin to all WebSocket /admins subscribers
                messagingTemplate.convertAndSend("/topic/admins", newAdmin.get());
                return ResponseEntity.ok().body(Map.of("message", "Admin created successfully"));
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Account creation failed. Please try again"));
        }
        catch (Exception e) {
            logger.error("An error occured: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }

    /**
     * authenticate admin credentials
     */
    @PostMapping("/public/auth")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AdminAuthenticationDTO adminAuthenticationDTO) {
        try {
            Optional<?> authenticateAdmin = adminService.authenticate(adminAuthenticationDTO);

            return authenticateAdmin.isEmpty() ?
                    ResponseEntity.badRequest().body(Map.of("error", "Invalid email or password")):
                    ResponseEntity.ok().body(authenticateAdmin);
        }
        catch (DataException dataException) {
            logger.error("Admin does not exist by id: {}", dataException.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Error database"));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }

    /**
     * GET all co-admins stored in database
     */
    @GetMapping("/private/get-all-co-admins")
    public ResponseEntity<?> getAllCoAdmins() {
        try {
            List<AdminDTO> allCoAdmins = adminService.getAllCoAdmins();

            return allCoAdmins.isEmpty() ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.ok().body(allCoAdmins);
        }
        catch (ResourceNotFoundException e) {
            logger.error("No co admin is available: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }


    @PostMapping("/private/accept-doctor-request/{requestId}")
    public ResponseEntity<?> acceptDoctorRequest(@PathVariable Long requestId) {
        try {
            Optional<Doctor> addedNewDoctor = adminService.acceptDoctorRequest(requestId);

            if(addedNewDoctor.isPresent()) {
                // Broadcast the new doctor to all WebSocket /doctors subscribers
                messagingTemplate.convertAndSend("/topic/doctors", addedNewDoctor.get());
                return ResponseEntity.accepted().body(Map.of("message", "Added new doctor"));
            }

            return ResponseEntity.notFound().build();
        }
        catch (DataException dataException) {
            logger.error("Pending doctor not found by id: {}", dataException.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Error database"));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }

    /**
     * Uses a non-entity class for updating a doctor
     * completeName, password, availableDays are updatable and not
     * included the email because of db constraints. So in the frontend,
     * the existing email will automatically include to pass in request body.
     */
    @PutMapping("/private/update-doctor/{doctorExistingId}")
    public ResponseEntity<?> updateDoctor(@PathVariable Long doctorExistingId, @RequestBody UpdateDoctorDTO doctorDTO) {
        try {
            UpdateDoctorDTO updatedDoctor = adminService.updateDoctor(doctorExistingId, doctorDTO);

            // Broadcast the updated doctor to all WebSocket subscribers
            messagingTemplate.convertAndSend("/topic/doctors", updatedDoctor);

            return ResponseEntity.ok().body(Map.of("message", "Update successful"));
        }
        catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("Error occurred updating doctor: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }

    /**
     * DELETE Doctor
     */
    @DeleteMapping("/private/delete-doctor/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        try {
            // delete the doctor
            adminService.deleteDoctor(id);

            // fetch all the doctors
            List<DoctorDTO> getAllDoctor = adminService.getAllDoctors();

            // Broadcast the deleted doctor to all WebSocket subscribers
            messagingTemplate.convertAndSend("/topic/doctors", getAllDoctor);

            return ResponseEntity.ok().body(Map.of("message", "Delete successfully"));
        }
        catch (ResourceNotFoundException e) {
            logger.error("Doctor not found by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Doctor not found with id: " + id));
        }
        catch (Exception e) {
            logger.error("Error deleting patient with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred while deleting the doctor"));
        }
    }

    /**
     * SEARCH Doctor
     */
    @GetMapping("/private/search-doctor")
    public ResponseEntity<?> searchDoctor(@RequestParam String keyword) {
        try {
            logger.info("Searching for doctors with keyword: {}", keyword);
            List<DoctorDTO> searchDoctor = adminService.searchDoctor(keyword);
            return ResponseEntity.ok().body(searchDoctor);
        } catch (Exception ex) {
            logger.error("Error searching for doctors: ", ex);
            return ResponseEntity.internalServerError().body("Server Error");
        }
    }

    /**
     * Get all doctors
     */
    @GetMapping("/private/get-all-doctors")
    public ResponseEntity<?> getAllDoctors() {
        try {
            List<DoctorDTO> allDoctors = adminService.getAllDoctors();

            return allDoctors.isEmpty() ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.ok().body(allDoctors);
        }
        catch (ResourceNotFoundException e) {
            logger.error("No doctor is available: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }

    /**
     * GET all pending doctors
     */
    @GetMapping("/private/get-all-pending-doctors")
    public ResponseEntity<?> getAllPendingDoctors() {
        try {
            List<PendingDoctorDTO> allPendingDoctors = adminService.getAllPendingDoctors();

            return allPendingDoctors.isEmpty() ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.ok().body(allPendingDoctors);
        }
        catch (ResourceNotFoundException e) {
            logger.error("No pending doctor is available: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }

    /**
     * Update patient
     * ID is important because it is unique in all patient table row.
     * Update will be based on existing id so id cannot and should not be updated.
     */
    @PutMapping("/private/update-patient/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable Long id, @RequestBody UpdatePatientDTO patient) {
        try {
            UpdatePatientDTO updatedPatient = adminService.updatePatient(id, patient);

            // Broadcast the updated patient to all WebSocket subscribers
            messagingTemplate.convertAndSend("/topic/patients", updatedPatient);

            return ResponseEntity.ok().body(Map.of("message", "Update successful"));
        }
        catch (ResourceNotFoundException e) {
            logger.error("Error occurred updating patient: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }

    /**
     * Delete patient by ID
     */
    @DeleteMapping("/private/delete-patient/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Long id) {
        try {
            // Delete the patient
            adminService.deletePatient(id);

            // Fetch all remaining patients after deletion
            List<PatientDTO> getAllPatients = adminService.getAllPatients();

            // Broadcast the updated list of patients to all WebSocket subscribers
            messagingTemplate.convertAndSend("/topic/patients", getAllPatients);

            return ResponseEntity.ok().body(Map.of("message", "Patient deleted successfully"));
        }
        catch (ResourceNotFoundException e) {
            logger.error("Patient not found by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Patient not found with id: " + id));
        }
        catch (Exception e) {
            logger.error("Error deleting patient with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred while deleting the patient"));
        }
    }

    /**
     * GET all patients
     */
    @GetMapping("/private/get-all-patients")
    public ResponseEntity<?> getAllPatient() {
        try {
            List<PatientDTO> allPatients = adminService.getAllPatients();

            return allPatients.isEmpty() ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.ok().body(allPatients);
        }
        catch (ResourceNotFoundException e) {
            logger.error("No patient is available: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
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
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String status
    ) {
        try {
            Optional<List<PatientDTO>> filterPatient = adminService.filterPatient(gender, age, status);

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
}
