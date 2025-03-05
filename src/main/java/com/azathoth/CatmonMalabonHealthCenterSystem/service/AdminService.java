package com.azathoth.CatmonMalabonHealthCenterSystem.service;

import com.azathoth.CatmonMalabonHealthCenterSystem.controller.AdminController;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.PendingDoctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.DoctorRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.PendingDoctorRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.utils.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

@Service
public class AdminService {

    private final PendingDoctorRepository pendingDoctorRepository;
    private final DoctorRepository doctorRepository;

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AdminService(PendingDoctorRepository pendingDoctorRepository, DoctorRepository doctorRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.pendingDoctorRepository = pendingDoctorRepository;
        this.doctorRepository = doctorRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public Optional<Doctor> acceptDoctorRequest(Long requestId) {
        try {
            Optional<PendingDoctor> requestingDoctor = pendingDoctorRepository.findById(requestId);

            if(requestingDoctor.isPresent()) {
                PendingDoctor pendingDoctor = requestingDoctor.get();
                // pass pending doctor's information to the doctor entity
                // appointment field will be assign in patient service
                Doctor newDoctor = new Doctor();
                newDoctor.setCompleteName(pendingDoctor.getCompleteName());
                newDoctor.setEmail(pendingDoctor.getEmail());
                newDoctor.setPassword(pendingDoctor.getPassword()); // password is already hashed in pending doctor service
                newDoctor.setAvailableDays(pendingDoctor.getAvailableDays());
                newDoctor.setRole(Role.DOCTOR); // assign a role
                // save a new doctor
                Doctor addedDoctor = doctorRepository.save(newDoctor);

                return Optional.of(addedDoctor);
            }

            return Optional.empty();
        }
        catch (HttpClientErrorException.NotFound notFound) {
            logger.error("Pending doctor is not available by id: {}", notFound.getMessage());
            return Optional.empty();
        }
    }
}
