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

    /**
     * loadUserByUsername(email) which find matching email from 2 repositories,
     * if the first repository (doctor repo) found a matching email, which means, the
     * login credential (email) is from doctor. Else from the admin.
     * After, it creates a user details by passing email, password and role.
     * In createUserDetails method, the chain of conditional statements checks role
     * if role matched from any of that statement, then it will grant an authority.
     * Else, throw exception.
     */
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

    // create user details and grant role for each user
    private UserDetails createUserDetails(String email, String password, String role) {
        if ("DOCTOR".equals(role)) {
            return new User(email, password, Collections.singleton(new SimpleGrantedAuthority("ROLE_DOCTOR")));
        } else if ("ADMIN".equals(role)) {
            return new User(email, password, Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        } else {
            throw new UsernameNotFoundException("Invalid role");
        }
    }
}
