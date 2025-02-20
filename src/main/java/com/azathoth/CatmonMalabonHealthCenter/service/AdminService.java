package com.azathoth.CatmonMalabonHealthCenter.service;

import com.azathoth.CatmonMalabonHealthCenter.model.Admin;
import com.azathoth.CatmonMalabonHealthCenter.model.Role;
import com.azathoth.CatmonMalabonHealthCenter.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;

    public AdminService(PasswordEncoder passwordEncoder, AdminRepository adminRepository) {
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
    }

    public Optional<Admin> createAccount(Admin admin) {
        admin.setRole(Role.ADMIN);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        Admin createdAdmin = adminRepository.save(admin);

        return Optional.of(createdAdmin);
    }
}
