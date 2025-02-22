package com.azathoth.CatmonMalabonHealthCenter.controller;

import com.azathoth.CatmonMalabonHealthCenter.model.Admin;
import com.azathoth.CatmonMalabonHealthCenter.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenter.model.utils.UpdateDoctor;
import com.azathoth.CatmonMalabonHealthCenter.service.AdminService;
import com.azathoth.CatmonMalabonHealthCenter.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    // stores error and good message as object to response as json
    private final Map<String, String> errorMessage = new HashMap<>();
    private final Map<String, String> goodMessage = new HashMap<>();

    private final AdminService adminService;
    private final DoctorService doctorService;

    public AdminController(AdminService adminService, DoctorService doctorService) {
        this.adminService = adminService;
        this.doctorService = doctorService;

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

            goodMessage.replace("message", "Successful registration");
            errorMessage.replace("error", "Invalid registration");

            return createdAdmin.isEmpty() ?
                    new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST) :
                    new ResponseEntity<>(goodMessage, HttpStatus.CREATED);
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

            goodMessage.replace("message", "Login successful");
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

            goodMessage.replace("message", "Update successful");
            errorMessage.replace("error", "Update failed");

            return  updatedDoctor.isEmpty() ?
                    new ResponseEntity<>(errorMessage, HttpStatus.NOT_ACCEPTABLE) :
                    new ResponseEntity<>(goodMessage, HttpStatus.OK);
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

            return deletedDoctor ?
                    new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                    new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid request");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
