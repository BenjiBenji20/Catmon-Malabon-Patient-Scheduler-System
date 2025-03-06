package com.azathoth.CatmonMalabonHealthCenterSystem.service;

import com.azathoth.CatmonMalabonHealthCenterSystem.controller.AdminController;
import com.azathoth.CatmonMalabonHealthCenterSystem.dto.AdminAuthenticationDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.dto.AdminDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Admin;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.PendingDoctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.AdminRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.DoctorRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.PendingDoctorRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.utils.Role;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

@Service
public class AdminService {

    private final PendingDoctorRepository pendingDoctorRepository;
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AdminService(PendingDoctorRepository pendingDoctorRepository, DoctorRepository doctorRepository, AdminRepository adminRepository, PasswordEncoder encoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.pendingDoctorRepository = pendingDoctorRepository;
        this.doctorRepository = doctorRepository;
        this.adminRepository = adminRepository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public Optional<Admin> createAccount(@Valid AdminDTO adminDTO) {
        Admin admin = adminDTO.convertToAdminEntity(adminDTO);

        admin.setPassword(encoder.encode(admin.getPassword()));

        // save created admin
        return Optional.of(adminRepository.save(admin));
    }

    public Optional<?> authenticate(@Valid AdminAuthenticationDTO adminAuthenticationDTO) {
        Authentication authenticateAdmin =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                adminAuthenticationDTO.getEmail(), adminAuthenticationDTO.getPassword()
                        )
                );

        if(authenticateAdmin.isAuthenticated()) {
            Admin authAdmin = adminRepository.findAdminByEmail(adminAuthenticationDTO.getEmail());

            return Optional.of(jwtService.generateToken(authAdmin.getEmail(), authAdmin.getRole().toString()));
        }

        return Optional.empty();
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
                // delete the newly added doctor from pending table
                pendingDoctorRepository.delete(pendingDoctor);
                // pass doctor obj as response to be use by websocket channel
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
