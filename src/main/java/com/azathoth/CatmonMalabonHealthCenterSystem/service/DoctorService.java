package com.azathoth.CatmonMalabonHealthCenterSystem.service;

import com.azathoth.CatmonMalabonHealthCenterSystem.dto.DoctorAuthenticationDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.DoctorRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public DoctorService(DoctorRepository doctorRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.doctorRepository = doctorRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public Optional<?> authenticateDoctor(@Valid DoctorAuthenticationDTO doctorDto) {
        Authentication authenticateDoctor =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                doctorDto.getEmail(), doctorDto.getPassword()
                        )
                );

        if(authenticateDoctor.isAuthenticated()) {
            Doctor authDoctor = doctorRepository.findDoctorByEmail(doctorDto.getEmail());

            return Optional.of(jwtService.generateToken(authDoctor.getEmail(), authDoctor.getRole().toString()));
        }

        return Optional.empty();

    }
}
