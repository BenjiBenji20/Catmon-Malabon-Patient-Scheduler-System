package com.azathoth.CatmonMalabonHealthCenterSystem.controller;

import com.azathoth.CatmonMalabonHealthCenterSystem.dto.DoctorAuthenticationDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.service.DoctorService;
import jakarta.validation.Valid;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
