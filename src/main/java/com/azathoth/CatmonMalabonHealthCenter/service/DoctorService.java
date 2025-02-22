package com.azathoth.CatmonMalabonHealthCenter.service;

import com.azathoth.CatmonMalabonHealthCenter.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenter.model.Role;
import com.azathoth.CatmonMalabonHealthCenter.repository.DoctorRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public DoctorService(DoctorRepository doctorRepository, PasswordEncoder passwordEncoder,
                         AuthenticationManager authenticationManager, JwtService jwtService) {
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Saves a new doctor to the db
     */
    public Optional<Doctor> register(Doctor newDoctor) {
        newDoctor.setRole(Role.DOCTOR); // set role
        newDoctor.setPassword(passwordEncoder.encode(newDoctor.getPassword())); // hash password
        Doctor addedDoctor = doctorRepository.save(newDoctor); // save the new doctor to the db

        return Optional.of(addedDoctor);
    }

    /**
     * login endpoint
     */
    public Optional<?> authenticate(Doctor doctor) {
        Authentication authenticateDoctor =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                doctor.getEmail(), doctor.getPassword()
                        )
                );

        if(authenticateDoctor.isAuthenticated()) {
            Doctor authDoctor = doctorRepository.findDoctorByEmail(doctor.getEmail());

            return Optional.of(jwtService.generateToken(authDoctor.getEmail(), authDoctor.getRole().toString()));
        }

        return Optional.empty();
    }
}
