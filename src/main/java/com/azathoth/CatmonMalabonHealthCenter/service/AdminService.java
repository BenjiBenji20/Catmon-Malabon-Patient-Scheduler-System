package com.azathoth.CatmonMalabonHealthCenter.service;

import com.azathoth.CatmonMalabonHealthCenter.model.Admin;
import com.azathoth.CatmonMalabonHealthCenter.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenter.model.Patient;
import com.azathoth.CatmonMalabonHealthCenter.model.Role;
import com.azathoth.CatmonMalabonHealthCenter.model.utils.UpdateDoctor;
import com.azathoth.CatmonMalabonHealthCenter.model.utils.UpdatePatient;
import com.azathoth.CatmonMalabonHealthCenter.repository.AdminRepository;
import com.azathoth.CatmonMalabonHealthCenter.repository.DoctorRepository;
import com.azathoth.CatmonMalabonHealthCenter.repository.PatientRepository;
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
    private final PatientRepository patientRepository;

    public AdminService(PasswordEncoder passwordEncoder, AdminRepository adminRepository, AuthenticationManager authenticationManager, JwtService jwtServic, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtServic;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
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

    /**
     * patient requesting for update will be found using its id.
     */
    public Optional<Patient> updatePatient(UpdatePatient patient) {
        boolean isPatientExist = patientRepository.existsById(patient.getId());

        if(!isPatientExist) {
            return Optional.empty();
        }

        Patient existingPatient = patientRepository.findPatientById(patient.getId());

        existingPatient.setCompleteName(patient.getNewCompleteName());
        existingPatient.setGender(patient.getNewGender());
        existingPatient.setAddress(patient.getNewAddress());
        existingPatient.setAge(patient.getNewAge());

        return Optional.of(patientRepository.save(existingPatient));
    }
}
