package com.azathoth.CatmonMalabonHealthCenter.controller;

import com.azathoth.CatmonMalabonHealthCenter.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenter.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Controller
@RequestMapping("/api/doctor")
public class DoctorController {
    // stores error and good message as object to response as json
    private final Map<String, String> errorMessage = new HashMap<>();
    private final Map<String, String> goodMessage = new HashMap<>();

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;

        errorMessage.put("error", "");
        goodMessage.put("message", "");
    }

    /**
     *
     * Doctor controller endpoint.
     * Doctor should pass request object with value: completeName,
     * email, password, and an array of availableDay enum
     */
    @PostMapping("register")
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

            goodMessage.replace("message", "Successful registration");
            errorMessage.replace("error", "Invalid registration");

            return addedNewDoctor.isEmpty() ?
                    new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST) :
                    new ResponseEntity<>(goodMessage, HttpStatus.CREATED);
        }
        catch (Exception e) {
            System.out.println("Error found: " + e.getMessage());
            errorMessage.replace("error", "Invalid registration");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
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
}
