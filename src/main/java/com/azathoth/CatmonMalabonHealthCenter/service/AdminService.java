package com.azathoth.CatmonMalabonHealthCenter.service;

import com.azathoth.CatmonMalabonHealthCenter.model.Admin;
import com.azathoth.CatmonMalabonHealthCenter.model.Role;
import com.azathoth.CatmonMalabonHealthCenter.repository.AdminRepository;
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
    private  final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AdminService(PasswordEncoder passwordEncoder, AdminRepository adminRepository, AuthenticationManager authenticationManager, JwtService jwtServic) {
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtServic;
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
            return Optional.of(jwtService.generateToken(admin.getEmail()));
        }

        return Optional.empty();
    }
}
