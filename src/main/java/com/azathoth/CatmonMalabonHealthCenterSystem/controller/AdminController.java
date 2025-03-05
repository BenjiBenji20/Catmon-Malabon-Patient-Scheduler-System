package com.azathoth.CatmonMalabonHealthCenterSystem.controller;

import com.azathoth.CatmonMalabonHealthCenterSystem.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.service.AdminService;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/public/accept-doctor-request/{requestId}")
    public ResponseEntity<?> acceptDoctorRequest(@PathVariable Long requestId) {
        try {
            Optional<Doctor> addedNewDoctor = adminService.acceptDoctorRequest(requestId);

            if(addedNewDoctor.isPresent()) {
                messagingTemplate.convertAndSend("/topic/doctors", addedNewDoctor.get());
                return ResponseEntity.accepted().body(Map.of("message", "Added new doctor"));
            }

            return ResponseEntity.notFound().build();
        }
        catch (DataException dataException) {
            logger.error("Pending doctor not found by id: {}", dataException.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Server error"));
        }
    }
}
