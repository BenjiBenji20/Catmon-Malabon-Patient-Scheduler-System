package com.azathoth.CatmonMalabonHealthCenter.service;

import com.azathoth.CatmonMalabonHealthCenter.model.Admin;
import com.azathoth.CatmonMalabonHealthCenter.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenter.model.Role;
import com.azathoth.CatmonMalabonHealthCenter.model.utils.UpdateDoctor;
import com.azathoth.CatmonMalabonHealthCenter.repository.AdminRepository;
import com.azathoth.CatmonMalabonHealthCenter.repository.DoctorRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final DoctorRepository doctorRepository;

    public AdminService(PasswordEncoder passwordEncoder, AdminRepository adminRepository, AuthenticationManager authenticationManager, JwtService jwtServic, DoctorRepository doctorRepository) {
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtServic;
        this.doctorRepository = doctorRepository;
    }

    public Optional<Admin> createAccount(Admin admin) {
        admin.setRole(Role.ADMIN);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        Admin createdAdmin = adminRepository.save(admin);

        return Optional.of(createdAdmin);
    }

    public Optional<?> authenticate(Admin admin) {
        Authentication authenticateAdmin =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                admin.getEmail(), admin.getPassword()
                        )
                );

        if(authenticateAdmin.isAuthenticated()) {
            Admin authAdmin = adminRepository.findAdminByEmail(admin.getEmail());

            return Optional.of(jwtService.generateToken(authAdmin.getEmail(), authAdmin.getRole().toString()));
        }

        return Optional.empty();
    }

    /**
     * Update doctor endpoint
     */
    public Optional<Doctor> updateDoctor(UpdateDoctor updateDoctor) {
        Doctor existingDoctor = doctorRepository.findDoctorByEmail(updateDoctor.getExistingEmail());
        if (existingDoctor == null) {
            return Optional.empty();
        }

        existingDoctor.setCompleteName(updateDoctor.getNewCompleteName());
        existingDoctor.setPassword(passwordEncoder.encode(updateDoctor.getNewPassword()));
        existingDoctor.setAvailableDays(updateDoctor.getNewAvailableDays());

        return Optional.of(doctorRepository.save(existingDoctor));
    }

    /**
     * delete doctor endpoint
     */
    public boolean deleteDoctor(Long id) {
        Optional<Doctor> doctor = doctorRepository.findById(id);

        if(doctor.isEmpty()) {
            return false;
        }

        // delete the doctor
        doctorRepository.deleteById(id);

        return true;
    }
}
