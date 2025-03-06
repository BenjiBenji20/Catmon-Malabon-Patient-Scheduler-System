package com.azathoth.CatmonMalabonHealthCenterSystem.controller;

import com.azathoth.CatmonMalabonHealthCenterSystem.dto.DoctorAuthenticationDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.dto.PatientDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.exception.ResourceNotFoundException;
import com.azathoth.CatmonMalabonHealthCenterSystem.service.DoctorService;
import jakarta.validation.Valid;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
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
}
