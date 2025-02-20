package com.azathoth.CatmonMalabonHealthCenter.service;

import com.azathoth.CatmonMalabonHealthCenter.model.Admin;
import com.azathoth.CatmonMalabonHealthCenter.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenter.repository.AdminRepository;
import com.azathoth.CatmonMalabonHealthCenter.repository.DoctorRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;

    public CustomUserDetailService(DoctorRepository doctorRepository, AdminRepository adminRepository) {
        this.doctorRepository = doctorRepository;
        this.adminRepository = adminRepository;
    }

    // I will also have admin role to check that's why I created a custom method
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Doctor doctor = doctorRepository.findDoctorByEmail(email);
        if (doctor != null) {
            return createUserDetails(doctor.getEmail(), doctor.getPassword(), doctor.getRole().toString());
        }

        Admin admin = adminRepository.findAdminByEmail(email);
        if (admin != null) {
            return createUserDetails(admin.getEmail(), admin.getPassword(), admin.getRole().toString());
        }

        throw new UsernameNotFoundException("User not found");
    }

    private UserDetails createUserDetails(String email, String password, String role) {
        if ("DOCTOR".equals(role)) {
            return new User(email, password, Collections.singleton(new SimpleGrantedAuthority("DOCTOR")));
        } else if ("ADMIN".equals(role)) {
            return new User(email, password, Collections.singleton(new SimpleGrantedAuthority("ADMIN")));
        } else {
            throw new UsernameNotFoundException("Invalid role");
        }
    }
}
