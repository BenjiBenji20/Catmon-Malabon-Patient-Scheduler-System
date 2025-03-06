package com.azathoth.CatmonMalabonHealthCenterSystem.controller;

import com.azathoth.CatmonMalabonHealthCenterSystem.dto.AdminAuthenticationDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.dto.AdminDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.dto.DoctorDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.dto.UpdateDoctorDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.exception.ResourceNotFoundException;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Admin;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.service.AdminService;
import jakarta.validation.Valid;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

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

            if(authenticateAdmin.isPresent()) {
                return ResponseEntity.ok().body(authenticateAdmin);
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Invalid email or password"));
        }
        catch (DataException dataException) {
            logger.error("Admin does not exist by id: {}", dataException.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Error database"));
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
}
