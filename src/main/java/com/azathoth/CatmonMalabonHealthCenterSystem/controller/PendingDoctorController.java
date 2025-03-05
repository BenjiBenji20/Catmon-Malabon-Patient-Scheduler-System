package com.azathoth.CatmonMalabonHealthCenterSystem.controller;

import com.azathoth.CatmonMalabonHealthCenterSystem.model.PendingDoctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.service.PendingDoctorService;
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
public class PendingDoctorController {

    private final PendingDoctorService pendingDoctorService;
    private static final Logger logger = LoggerFactory.getLogger(PendingDoctorController.class);

    public PendingDoctorController(PendingDoctorService pendingDoctorService) {
        this.pendingDoctorService = pendingDoctorService;
    }

    @PostMapping("/public/request-for-registration")
    public ResponseEntity<?> requestRegister(@Valid @RequestBody PendingDoctor requestRegistration) {
        try {
            Optional<PendingDoctor> pendingDoctor = pendingDoctorService.requestRegister(requestRegistration);

            if(pendingDoctor.isPresent()) {
                return ResponseEntity.ok().body(Map.of("message", "Your request is delivered. Please wait to accept by admin"));
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Request failed. Please try again."));
        }
        catch (DataException d) {
            logger.error("Database isn't available: {}", d.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("An error occurred: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Server cannot response"));
        }
    }
}
